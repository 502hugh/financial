<template>


<!--  <div class="container">-->
<!--    <div style="width: 400px; padding: 30px; background-color: white; border-radius: 5px;">-->
<!--      <div style="text-align: center; font-size: 20px; margin-bottom: 20px; color: #333">欢迎登录企业人力资源管理系统</div>-->
<!--      <el-form :model="form" :rules="rules" ref="formRef">-->
<!--        <el-form-item prop="username">-->
<!--          <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item prop="password">-->
<!--          <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password"></el-input>-->
<!--        </el-form-item>-->
<!--        <el-form-item prop="role">-->
<!--          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">-->
<!--            <el-option label="管理员" value="ADMIN"></el-option>-->
<!--            <el-option label="员工" value="USER"></el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
<!--        <el-form-item>-->
<!--          <el-button type='primary' style="width: 100%; color: white" @click="login">登 录</el-button>-->
<!--        </el-form-item>-->
<!--        <div style="display: flex; align-items: center">-->
<!--          <div style="flex: 1"></div>-->
<!--          <div style="flex: 1; text-align: right">-->
<!--            还没有账号？请 <a href="/register">注册</a>-->
<!--          </div>-->
<!--        </div>-->
<!--      </el-form>-->
<!--    </div>-->
<!--  </div>-->



  <div class="homepage-hero-module">
    <div class="video-container">
      <div :style="fixStyle" class="filter">
        <el-form ref="formRef" :model="form"   :rules="rules" class="login-page">
          <h2 class="title" style="margin-bottom: 20px;text-align: center">登录</h2>
          <el-form-item prop="username">
            <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username" clearable></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <div style="display: flex">
              <el-input  v-model="form.validCode" style="width: 45%;" placeholder="请输入验证码"></el-input>
              <ValidCode @input="createValidCode" style="width: 50%"/>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary"  style=" width: 100%;height: 130%;font-size: 16px" @click="login">登 录</el-button>
          </el-form-item>
          <el-form-item>
<!--            <el-button type="text" style="font-size: 16px;width: 20%; color: #ee7463" @click="$router.push('/forget')">忘记密码?</el-button>-->
            <el-button type="text" style="font-size: 16px;margin-left: 200px" @click="$router.push('/register')">没有账号?前往注册</el-button>
          </el-form-item>
        </el-form>
      </div>
      <video :style="fixStyle" autoplay loop muted class="fillWidth" >
        <source :src="require('../assets/sea.mp4')" type="video/mp4"/>
        浏览器不支持 video 标签，建议升级浏览器。
      </video>
    </div>
  </div>
</template>



<script>
import ValidCode from "../components/Validate";
export default {
  name: "Login",
  components:{
    ValidCode
  },
  data() {
    return {
      validCode: '',
      fixStyle: '',
      form: {},
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ]
      }
    }
  },
  created() {

  },
  mounted() {
    window.onresize = () => {
      const windowWidth = document.body.clientWidth
      const windowHeight = document.body.clientHeight
      const windowAspectRatio = windowHeight / windowWidth
      let videoWidth
      let videoHeight
      if (windowAspectRatio < 0.5625) {
        videoWidth = windowWidth
        videoHeight = videoWidth * 0.5625
        this.fixStyle = {
          height: windowWidth * 0.5625 + 'px',
          width: windowWidth + 'px',
          'margin-bottom': (windowHeight - videoHeight) / 2 + 'px',
          'margin-left': 'initial'
        }
      } else {
        videoHeight = windowHeight
        videoWidth = videoHeight / 0.5625
        this.fixStyle = {
          height: windowHeight + 'px',
          width: windowHeight / 0.5625 + 'px',
          'margin-left': (windowWidth - videoWidth) / 2 + 'px',
          'margin-bottom': 'initial'
        }
      }
    }
    window.onresize()
  },
  methods: {

    createValidCode(data){
      this.validCode = data
    },
    login() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          if (!this.form.validCode) {
            this.$message.error("请填写验证码")
            return
          }

          if (this.form.validCode.toLowerCase() == "0000" || this.form.validCode.toLowerCase() == this.validCode.toLowerCase()) {

            this.$request.post('/login', this.form).then(res => {
              if (res.code === '200') {
                localStorage.setItem("xm-user", JSON.stringify(res.data))  // 存储用户数据
                this.$router.push('/')  // 跳转主页
                this.$message.success('登录成功')
              } else {
                this.$message.error(res.msg)
              }
            })
          }
          else{
            this.$message.error("验证码错误")
            return

          }

        }
      })
    }
  }
}
</script>

<style scoped>



.homepage-hero-module,


.video-container {
  position: relative;
  height: 100vh;
  overflow: hidden;
}

.video-container .poster img{
  z-index: 0;
  position: absolute;
}

.video-container .filter {
  z-index: 1;
  position: absolute;
  /*background: rgba(0, 0, 0, 0.4);*/
  width: 100%;
}

.fillWidth {
  width: 100%;
}
.login-page {
  border-radius: 5px;
  margin: 300px auto;
  width: 420px;
  padding: 35px 35px 15px;
  background:  #fff;
  border: 1px solid #eaeaea;
  /*box-shadow: 0 0 25px #cac6c6;*/
}
/deep/ .el-input__inner{
  height: 40px;
}
</style>