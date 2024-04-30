<template>
  <div class="homepage-hero-module">
    <div class="video-container">
      <div :style="fixStyle" class="filter">
        <el-form ref="formRef" :model="form"   :rules="rules" class="login-page">
          <h2 class="title" style="margin-bottom: 20px;text-align: center">管理员注册</h2>
          <el-form-item prop="username">
            <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password  v-model="form.password"></el-input>
          </el-form-item>
          <el-form-item prop="confirmPass">
            <el-input prefix-icon="el-icon-lock" placeholder="请确认密码" show-password  v-model="form.confirmPass"></el-input>
          </el-form-item>
<!--          <el-form-item prop="role">-->
<!--            <el-select v-model="form.role" placeholder="请选择角色"  :style="{ width: '100%' }">-->
<!--              <el-option label="管理员" value="ADMIN">管理员</el-option>-->
<!--              <el-option label="员工"   value="USER">员工</el-option>-->
<!--            </el-select>-->
<!--            &lt;!&ndash;          <el-input v-model="form.level" placeholder="身份"></el-input>&ndash;&gt;-->
<!--          </el-form-item>-->
          <el-form-item>
            <div style="display: flex">
              <el-input  v-model="form.validCode" style="width: 45%;" placeholder="请输入验证码"></el-input>
              <ValidCode @input="createValidCode" style="width: 50%" />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style=" width: 100%;height: 130%;font-size: 16px"  @click="register">注 册</el-button>
          </el-form-item>
<!--          <div style="display: flex; align-items: center">-->
<!--            <div style="flex: 1"></div>-->
<!--            <div style="flex: 1; text-align: right">-->
<!--              已有账号？请 <a href="/login">登录</a>-->
<!--            </div>-->
<!--          </div>-->
          <el-form-item>
            <el-button type="text" style="font-size: 16px;margin-left: 200px" @click="$router.push('/login')">前往登录>></el-button>
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
import ValidCode from "@/components/Validate.vue";

export default {
  name: "RegisterAdmin",
  components:{
    ValidCode
  },
  data() {
    // 验证码校验
    const validatePassword = (rule, confirmPass, callback) => {
      if (confirmPass === '') {
        callback(new Error('请确认密码'))
      } else if (confirmPass !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      validCode: '',
      fixStyle:'',
      form: {},
      rules: {
        username: [
          { required: true, message: '请输入账号', trigger: 'blur' },
          {
            type: 'string',
            message: '用户名不能包含空格',
            trigger: 'blur',
            transform(value) {
              if (value && value.indexOf(' ') === -1) {
                return value
              } else {
                return false
              }
            }
          },
          {
            max:10,
            trigger: 'blur',
            message: '用户名长度不长于10位',

          },
          {
            trigger: 'blur',
            validator: (rule, value, callback) => {
              // var passwordreg = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[A-Za-z0-9]/
              var passwordreg = /(?=.*\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z\d]/
              if (!passwordreg.test(value)) {
                callback(new Error('用户名必须由数字、小写字母、大写字母组合'))
              } else {
                callback()
              }
            }
          }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          {
            type: 'string',
            message: '密码不能包含空格',
            trigger: 'blur',
            transform(value) {
              if (value && value.indexOf(' ') === -1) {
                return value
              } else {
                return false
              }
            }
          },
          {
            min:6,
            trigger: 'blur',
            message: '密码长度不短于6位',

          },
          {
            max:16,
            trigger: 'blur',
            message: '密码长度不长于16位',

          },

          {
            trigger: 'blur',
            validator: (rule, value, callback) => {
              var passwordreg = /(?=.*\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z\d]).{6,16}/
              if (!passwordreg.test(value)) {
                callback(new Error('密码必须由数字、字母、特殊字符组合'))
              }else{
                callback()
              }
            }
          }
        ],
        confirmPass: [
          { validator: validatePassword, trigger: 'blur' }
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
    register() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          if (!this.form.validCode) {
            this.$message.error("请填写验证码")
            return
          }


          if (this.form.validCode.toLowerCase() == "0000" || this.form.validCode.toLowerCase() == this.validCode.toLowerCase()) {

            console.log(this.form)
            this.form.role = "ADMIN"
            this.$request.post('/register', this.form).then(res => {
              if (res.code === '200') {
                this.$router.push('/')  // 跳转登录页面
                this.$message.success('注册成功')
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