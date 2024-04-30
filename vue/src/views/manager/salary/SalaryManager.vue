
<template>
  <div>
    <div class="search">
      <el-input placeholder="请输员工姓名" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      <el-button v-if="user.role === 'ADMIN'  || employee.powerName === '财务'" type="warning" plain style="margin-left: 10px" @click="handlegrant">一键发放</el-button>
    </div>

    <div v-if="user.role === 'ADMIN' || employee.powerName === '财务'" class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">

        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="employeeName" label="员工姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="basicSalary" label="基础工资" ></el-table-column>
        <el-table-column prop="positionSalary" label="岗位工资" ></el-table-column>
        <el-table-column prop="performanceBonus" label="绩效奖金" ></el-table-column>
        <el-table-column prop="otherAllowance" label="其他津贴" ></el-table-column>
        <el-table-column prop="socialInsurance" label="社保缴纳情况" ></el-table-column>
        <el-table-column prop="housingFund" label="公积金缴纳情况" ></el-table-column>
        <el-table-column prop="personalIncomeTax" label="个人所得税" ></el-table-column>
        <el-table-column prop="salaryDate" label="薪资发放日期" ></el-table-column>
        <el-table-column v-if="user.role === 'ADMIN' || employee.powerName === '财务'" label="操作" width="250" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini">编辑</el-button>
            <el-button plain type="primary" @click="grantone(scope.row)" size="mini">发放</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>删除</el-button>
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


    <el-dialog title="薪资配置" :visible.sync="fromVisible" width="50%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item v-if="employeeSelect === 0" prop="employeeId" label="选择员工" :error="errorMessages.employeeId">
          <el-select v-model="form.employeeId" placeholder="请选择员工" style="width: 100%" @change="handleEmployeeSelectChange">
            <el-option v-for="item in employeeData" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="basicSalary" label="基础工资">
          <el-input v-model="form.basicSalary" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="positionSalary" label="岗位工资">
          <el-input v-model="form.positionSalary" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="performanceBonus" label="绩效奖金">
          <el-input v-model="form.performanceBonus" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="otherAllowance" label="其他津贴">
          <el-input v-model="form.otherAllowance" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="socialInsurance" label="社保缴纳情况">
          <el-input v-model="form.socialInsurance" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="housingFund" label="公积金缴纳情况">
          <el-input v-model="form.housingFund" autocomplete="off"></el-input>
        </el-form-item>
<!--        <el-form-item prop="personalIncomeTax" label="个人所得税">-->
<!--          <el-input v-model="form.personalIncomeTax" autocomplete="off"></el-input>-->
<!--        </el-form-item>-->
        <el-form-item prop="salaryDate" label="薪资发放日期">
          <el-date-picker style="width: 100%"
                          v-model="form.salaryDate"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false,employeeSelect = 0">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>


    <el-dialog title="一键发放" :visible.sync="timeSet" width="50%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="salaryDate" label="薪资发放日期">
          <el-date-picker style="width: 100%"
                          v-model="form.salaryDate"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="timeSet = false">取 消</el-button>
        <el-button type="primary" @click="grant">确 定</el-button>
      </div>
    </el-dialog>


  </div>

</template>

<script>

export default {
  name: "SalaryManager",

  data(){
    return{
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      fromVisible:false,
      form:{},

      name:null,
      employee:"",
      employeeData:[],
      errorMessages: {
        employeeId: '' // 员工ID的错误信息
        // 其他表单项的错误信息
      },
      rules:{
        employeeId:[ {required: true, message: '请选择员工', trigger: 'blur'},],
        basicSalary:[
          {required: true, message: '请添加基础工资', trigger: 'blur'},
          {
            validator: (rule, value, callback) => {
              if (isNaN(value) || value <= 0) {
                callback(new Error('请输入大于0的数字'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          },
          {
            pattern: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
            message: '请输入正确的金额，最多保留两位小数',
            trigger: 'blur'
          }
        ],
        performanceBonus:[
          {
            validator: (rule, value, callback) => {
              if (isNaN(value) || value <= 0) {
                callback(new Error('请输入大于0的数字'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          },
          {
            pattern: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
            message: '请输入正确的金额，最多保留两位小数',
            trigger: 'blur'
          }
        ],
        otherAllowance:[
          {
            validator: (rule, value, callback) => {
              if (isNaN(value) || value <= 0) {
                callback(new Error('请输入大于0的数字'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          },
          {
            pattern: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
            message: '请输入正确的金额，最多保留两位小数',
            trigger: 'blur'
          }
        ],
        positionSalary:[
          {required: true, message: '请添加岗位工资', trigger: 'blur'},
          {
            validator: (rule, value, callback) => {
              if (isNaN(value) || value <= 0) {
                callback(new Error('请输入大于0的数字'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          },
          {
            pattern: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
            message: '请输入正确的金额，最多保留两位小数',
            trigger: 'blur'
          }
        ],
      },
      employeeSelect:0,
      ids: [],
      timeSet:false,
      grantDate:null,
    }
  },
  mounted() {
    this.loadEmployee()
  },
  created() {
    this.load(1)
    this.loadEmployeeDate()
  },
  methods:{

    grantone(row){
      this.$request.post('/salaryManager/grantOne/'+row.id).then(res => {
        if (res.code === '200') {   // 表示操作成功
          this.$message.success('操作成功')
          this.timeSet = false
          this.load(1)

        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },

    handlegrant(){
      this.form = {}
      this.timeSet = true
    },

    grant(){
      this.$request.post('/salaryManager/grant',this.form).then(res => {
        if (res.code === '200') {   // 表示操作成功
          this.$message.success('操作成功')
          this.timeSet = false
          this.load(1)
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    handleEmployeeSelectChange(){

      this.$request.get('/salaryManager/selectByEmployeeId/' + this.form.employeeId).then(res => {
        if (res.code === '200') {
          this.errorMessages.employeeId = ''; // 清空错误信息
        } else {
          this.errorMessages.employeeId = res.msg; // 设置错误信息
        }
      })
    },
    handleAdd(){
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    delBatch(){
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/salaryManager/delete/batch', {data: this.ids}).then(res => {
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

    save(){
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/salaryManager/update' : '/salaryManager/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.load(1)
              this.fromVisible = false
              this.employeeSelect = 0
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },

    handleEdit(row){
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
      this.employeeSelect = 1;
    },
    del(id){
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/salaryManager/delete/' + id).then(res => {
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

    handleSelectionChange(rows){
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/salaryManager/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          employeeName: this.name,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },

    reset() {
      this.name = null
      this.load(1)
    },


    handleCurrentChange(pageNum){
      this.load(pageNum)
    },


    loadEmployeeDate() {
      this.$request.get('/employee/selectAll').then(res => {
        if (res.code === '200') {
          this.employeeData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
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

</style>