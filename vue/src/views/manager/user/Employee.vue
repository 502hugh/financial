<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入账号查询" style="width: 200px" v-model="username"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      <el-button v-if="user.role === 'ADMIN'" type="primary" plain style="margin-left: 10px" @click="downloadExcel">导出员工信息</el-button>
      <el-button v-if="user.role === 'ADMIN' " type="primary" plain style="margin-left: 10px" @click="downloadExcelM">导出上传模板</el-button>
      <el-button v-if="user.role === 'ADMIN'" type="primary" plain style="margin-left: 10px" @click="excelIn">导入员工信息 </el-button>
    </div>


    <!-- 上传对话框    -->
    <el-dialog title="上传模板" :visible.sync="dialogOfUpload" width="35%" style="text-align: center;">
      <el-upload class="upload-demo" action="#"  drag multiple  :auto-upload="false"
                 :file-list="fileListExcel" :on-change="handleChangeExcel" >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">上传Excel格式文件</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogOfUpload = false">取 消</el-button>
        <el-button type="primary" @click="employeeUpload()">上 传</el-button>
      </div>
    </el-dialog>


    <div v-if="user.role === 'ADMIN' || employeePower.powerName === '人事'" class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button v-if="user.role === 'ADMIN'"  type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" strip @selection-change="handleSelectionChange">
        <el-table-column v-if="user.role === 'ADMIN'" type="selection" width="55" align="center"></el-table-column>
<!--        <el-table-column prop="id" label="序号" width="70" align="center" sortable></el-table-column>-->
        <el-table-column prop="username" label="账号"></el-table-column>
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="departmentName" label="部门"></el-table-column>
        <el-table-column prop="level" label="身份" >
          <template slot-scope="scope">
            <span v-if="scope.row.level === 1">员工</span>
            <span v-if="scope.row.level === 2">部长</span>
          </template>
        </el-table-column>
        <el-table-column v-if="user.role == 'ADMIN'"  prop="role" label="角色"></el-table-column>

        <el-table-column label="头像">
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px; border-radius: 50%" v-if="scope.row.avatar"
                        :src="scope.row.avatar" :preview-src-list="[scope.row.avatar]"></el-image>
            </div>
          </template>
        </el-table-column>

        <el-table-column  v-if="employee.powerName === '人事' || user.role === 'ADMIN'" label="操作" align="center" width="180">
          <template v-slot="scope">
            <el-button   size="mini" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button   v-if="user.level === 2 || user.role === 'ADMIN'" size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>


    <el-dialog title="员工" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="用户名" prop="username" :error="errorMessages.username">
          <el-input v-model="form.username" placeholder="用户名" @change="handleUserNameSelectChange"></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="电话"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="邮箱"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="level">
          <el-select v-model="form.departmentId" placeholder="请选择部门" style="width: 100%">
            <el-option v-for="item in departmentData" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="身份">
          <el-radio-group v-model="form.level">
            <el-radio :label="1" value="1">员工</el-radio>
            <el-radio :label="2" value="2">部长</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handleAvatarSuccess"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import department from "@/views/manager/Department.vue";
import axios from "axios";

export default {
  name: "Employee",
  computed: {
    department() {
      return department
    }
  },
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,     // 当前的页码
      pageSize: 10,   // 每页显示的个数
      total: 0,
      username: null,
      fromVisible: false,
      employee:"",
      form: {

      },
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        username: [
          {required: true, message: '请输入账号', trigger: 'blur'},
        ],
        phone: [
          {
            pattern: /^1[3456789]\d{9}$/,
            message: '手机号码格式不正确',
            trigger: 'blur'
          },
        ],

        email: [
          {
            pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/,
            message: '邮箱地址格式不正确',
            trigger: 'blur'
          },
        ],

      },
      errorMessages: {
        username:'',//用户名的错误
        // 其他表单项的错误信息
      },
      ids: [],
      employeePower: "",
      dialogOfUpload:false,
      fileListExcel: [],
      headers:"'Content-Type': 'multipart/form-data'",
    }
  },
  mounted() {
    this.loadBefor()
    this.loadEmployeePower()
    this.handleUserNameSelectChange()
    setTimeout(() => {
      // 在延迟后执行相关操作

    }, 200);
  },
  created() {
    this.load(1)
    this.loadDepartments()
  },
  methods: {
    excelIn(){
      this.dialogOfUpload = true
    },
    handleChangeExcel(file,fileListExcel){//文件数量改变
      // this.fileListExcel = fileListExcel;
      // 清空 fileListExcel 数组
      this.fileListExcel = [];

      // // 如果有上传的文件，则将新文件加入到 fileListExcel 数组中
      if (file && fileListExcel) {
        this.fileListExcel.push(fileListExcel[fileListExcel.length-1]);
      }
    },
    employeeUpload() {
      var param = new FormData();
      //单多文件都可以
      this.fileListExcel.forEach(
          (val, index) => {
            param.append("file", val.raw);
          }
      );

      axios.post(this.$baseUrl + "/excel/employeeIn", param,{
        headers: {
          'Content-Type': 'multipart/form-data',
          token: this.user.token,
        }
      }).then(res => {
        console.log(res)
        if(res.data.code ==="200"){
          this.$message.success(res.data.data);
        }else if(res.data.code ==="5099"){
          this.$message.warning(res.data.msg);
        }else{
          this.$message.error(res.data.msg);
        }
      });
      this.fileListExcel = [];
      this.dialogOfUpload = false;


    },

    downloadExcelM(){
      // 发送请求到后端，下载文件
      this.$request.post('/excel/employeeM', this.form, { responseType: 'blob' })
          .then(res => {
            let blob = new Blob([res], { type: 'application/xlsx' });
            let url = window.URL.createObjectURL(blob);
            const link = document.createElement('a'); //创建a标签
            link.href = url;
            link.download = '员工模板.xlsx'; //重命名文件
            link.click();
            URL.revokeObjectURL(url);
          })
          .catch(error => {
            this.$message.error('下载文件失败', error);
          });
    },
    downloadExcel() {
      // 发送请求到后端，下载文件
      this.$request.post('/excel/employeeOut', this.form, { responseType: 'blob' })
          .then(res => {
            let blob = new Blob([res], { type: 'application/xlsx' });
            let url = window.URL.createObjectURL(blob);
            const link = document.createElement('a'); //创建a标签
            link.href = url;
            link.download = '员工表.xlsx'; //重命名文件
            link.click();
            URL.revokeObjectURL(url);
          })
          .catch(error => {
            this.$message.error('下载文件失败', error);
          });
    },

    handleUserNameSelectChange(){
      this.$request.get('/employee/selectByUsername/' + this.form.username).then(res => {
        if (res.code === '200') {
          this.errorMessages.username = ''; // 清空错误信息
        } else {
          this.errorMessages.username = res.msg; // 设置错误信息
        }
      })
    },
    loadEmployeePower(){
      //获取当前员工用户的信息
      this.$request.get('/employee/selectPowerName').then(res => {
        if (res.code === '200') {
          this.employeePower = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    loadDepartments() {
      this.$request.get('/department/selectAll').then(res => {
        if (res.code === '200') {
          this.departmentData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    handleAdd() {   // 新增数据
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },

    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },

    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if(typeof(this.form.departmentId) == "undefined" && this.form.level != null){
          this.$message.error("请选择部门")
        }
        else if(this.form.departmentId != null && typeof(this.form.level) == "undefined"){
          this.$message.error("请选择身份")
        }else{
            if (valid) {
          this.$request({
            url: this.form.id ? '/employee/update' : '/employee/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
        }
      })
    },

    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/employee/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/employee/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    loadBefor(){
      //获取信息
      this.$request.get('/employee/selectPowerName').then(res => {
        if (res.code === '200') {
          this.employee = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/employee/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.username = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      // 把头像属性换成上传的图片的链接
      this.form.avatar = response.data
    },
  }
}
</script>

<style scoped>

</style>