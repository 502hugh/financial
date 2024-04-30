<template>
  <div class="manager-container">
    <!--  头部  -->
    <div class="manager-header">
      <div class="manager-header-left">
        <img src="@/assets/imgs/caiwu.svg" />
        <div class="title">财务管理</div>
      </div>

      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="manager-header-right">
        <el-dropdown placement="bottom">
          <div class="avatar">
            <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <div>{{ user.name ||  "用户" }}</div>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="goToPerson">个人信息</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/password')">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!--  主体  -->
    <div class="manager-main">
      <!--  侧边栏  -->
      <div v-if="user.role === 'ADMIN'" class="manager-main-left">
        <el-menu :default-openeds="['info', 'user','salary','log','matters','zongzhang']" router style="border: none" :default-active="$route.path">

          <el-menu-item index="/home">
            <template slot="title">
              <img src="@/assets/imgs/home.svg"/><span></span><span>&nbsp系统首页</span>
            </template>
          </el-menu-item>

          <el-submenu index="info">
            <template slot="title">
              <img src="@/assets/imgs/message.svg"/><span> </span><span>  信息管理</span>
            </template>
            <el-menu-item index="/notice">公告通知</el-menu-item>
            <el-menu-item index="/department">部门架构</el-menu-item>
          </el-submenu>

          <el-submenu index="user">
            <template slot="title">
              <img src="@/assets/imgs/user.svg"/><span> </span><span>  用户管理</span>
            </template>
            <el-menu-item index="/admin">管理员</el-menu-item>
            <el-menu-item index="/employee">员工</el-menu-item>
          </el-submenu>
          <el-submenu index="log">
            <template slot="title">
              <img src="@/assets/imgs/log.svg"/><span> </span><span>  日志管理</span>
            </template>
            <el-menu-item index="/log">日志信息</el-menu-item>
          </el-submenu>

          <el-submenu index="salary">
            <template slot="title">
              <img src="@/assets/imgs/salary.svg"/><span> </span><span>  薪资管理</span>
            </template>
            <el-menu-item index="/salary">薪资信息</el-menu-item>
            <el-menu-item index="/salaryManager">薪资配置</el-menu-item>
          </el-submenu>


          <el-submenu index="matters">
            <template slot="title">
              <img src="@/assets/imgs/resources.svg"/><span> </span><span>  资产管理</span>
            </template>
            <el-menu-item index="/resources">公司资产</el-menu-item>
            <el-menu-item index="/resourcesMy">审批记录</el-menu-item>
          </el-submenu>

          <el-submenu index="zongzhang">
            <template slot="title">
              <img src="@/assets/imgs/financial.svg"/><span> </span><span> 账务管理</span>
            </template>
            <el-menu-item index="/reimbursement">报账</el-menu-item>
            <el-menu-item index="/financialout">财务支出</el-menu-item>
            <el-menu-item index="/financialin">财务收入</el-menu-item>
          </el-submenu>
        </el-menu>
      </div>


      <div v-if="user.role === 'USER'" class="manager-main-left">
        <el-menu :default-openeds="['user','resources','salary', 'zongzhang']" router style="border: none" :default-active="$route.path">
          <el-menu-item index="/home">
            <template slot="title">
              <img src="@/assets/imgs/home.svg"/><span> </span><span>  系统首页</span>
            </template>
          </el-menu-item>

          <el-submenu index="user">
            <template slot="title">
              <img src="@/assets/imgs/message.svg"/><span> </span><span>  公司信息</span>
            </template>
            <el-menu-item index="/employee">员工信息</el-menu-item>
            <el-menu-item index="/department">部门架构</el-menu-item>
            <el-menu-item  v-if="employee.powerName === '财务'" index="/notice">公告通知</el-menu-item>
          </el-submenu>

          <el-submenu index="salary">
            <template slot="title">
              <img src="@/assets/imgs/salary.svg"/><span> </span><span>  薪资信息</span>
            </template>
            <el-menu-item index="/salary">个人薪资</el-menu-item>
            <el-menu-item v-if="employee.powerName === '财务'" index="/salaryManager">薪资配置</el-menu-item>
          </el-submenu>

          <el-submenu index="resources">
            <template slot="title">
              <img src="@/assets/imgs/resources.svg"/><span> </span><span>  资产信息</span>
            </template>
            <el-menu-item index="/resources">公司资产</el-menu-item>
            <el-menu-item index="/resourcesMy">申请记录</el-menu-item>
            <el-menu-item v-if="user.level === 2 || employee.powerName === '财务'" index="/resourcesApply">资产审批</el-menu-item>
          </el-submenu>


          <el-submenu index="zongzhang">
            <template slot="title">
              <img src="@/assets/imgs/financial.svg"/><span> </span><span> 总账</span>
            </template>
            <el-menu-item  index="/reimbursement">报账</el-menu-item>
            <el-menu-item v-if="employee.powerName === '财务'" index="/reimbursementApply">报账审批</el-menu-item>
            <el-menu-item v-if="user.level === 2" index="/financialout">财务支出</el-menu-item>
            <el-menu-item v-if="user.level === 2" index="/financialin">财务收入</el-menu-item>
          </el-submenu>


        </el-menu>
      </div>
      <!--  数据表格  -->
      <div class="manager-main-right">
        <router-view @update:user="updateUser" />
      </div>
    </div>

  </div>
</template>

<script>
import employee from "@/views/manager/user/Employee.vue";

export default {
  name: "Manager",
  data() {
    return {
      employee: "",
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    }
  },
  mounted() {
    this.loadEmployee()
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/login')
    }
  },
  methods: {

    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}')   // 重新获取下用户的最新信息
    },
    goToPerson() {
      if (this.user.role === 'ADMIN') {
        this.$router.push('/adminPerson')
      }
      if (this.user.role === 'USER') {
        this.$router.push('/employeePerson')
      }
    },
    logout() {
      localStorage.removeItem('xm-user')
      this.$router.push('/login')
    },
    loadEmployee(){
      //获取当前员工用户的信息
      this.$request.get('/employee/selectPowerName').then(res => {
        if (res.code === '200') {
          this.employee = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    } ,

  }
}
</script>

<style scoped>
@import "@/assets/css/manager.css";
</style>