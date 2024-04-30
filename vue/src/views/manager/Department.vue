<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入部门名称查询" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div v-if="user.role === 'ADMIN'" class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column v-if="user.role === 'ADMIN'" type="selection" width="55" align="center"></el-table-column>
<!--        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>-->
        <el-table-column prop="name" label="部门名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="description" label="部门描述" show-overflow-tooltip></el-table-column>
        <el-table-column prop="employeeName" label="部门部长"></el-table-column>
        <el-table-column prop="powerName" label="部门权限"></el-table-column>

        <el-table-column v-if="user.role === 'ADMIN'" label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini">编辑</el-button>
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


    <el-dialog title="部门信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="name" label="部门名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="description" label="部门描述">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="description" label="部门部长" :error="errorMessages.employeeId">
          <el-select v-model="form.employeeId" placeholder="请选择主管"  style="width: 100%" @change="handleEmployeeSelectChange">
            <el-option v-for="item in headerData" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="description" label="部门权限" :error="errorMessages.powerName">
          <el-select v-model="form.powerName" placeholder="请选择权限" style="width: 100%" @change="handlePowerNameSelectChange">
            <el-option v-for="(item, index) in powerOptions" :key="index" :label="item.label" :value="item.value"></el-option>
          </el-select>
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
export default {
  name: "Department",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        name: [
          {required: true, message: '请输入部门名称', trigger: 'blur'},
        ],
        employeeId: [
          {required: true, message: '请选择部门主管', trigger: 'blur'},
        ]
      },
      errorMessages: {
        employeeId: '', // 员工ID的错误信息
        powerName:'',//部门权限的错误
        // 其他表单项的错误信息
      },
      powerOptions: [
        { label: '财务', value: '财务' },
        { label: '人事', value: '人事' },
        { label: '运营', value: '运营' },
        { label: '技术', value: '技术' },
        { label: '采购', value: '采购' },
        { label: '销售', value: '销售' },
      ],
      ids: [],
      headerData: [],
      employee: '',
    }
  },

  mounted() {
    this.loadEmployee()
  },
  created() {
    this.load(1)
    this.loadHeaderData()
  },
  methods: {
    handlePowerNameSelectChange(){
      this.$request.get('/department/selectByPowerName/' + this.form.powerName).then(res => {
        if (res.code === '200') {
          this.errorMessages.powerName = ''; // 清空错误信息
        } else {
          this.errorMessages.powerName = res.msg; // 设置错误信息
        }
      })
    },

    handleEmployeeSelectChange(){

      this.$request.get('/department/selectByEmployeeId/' + this.form.employeeId).then(res => {
        if (res.code === '200') {
          this.errorMessages.employeeId = ''; // 清空错误信息
        } else {
          this.errorMessages.employeeId = res.msg; // 设置错误信息
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
    loadHeaderData() {
      this.$request.get('/employee/selectHeaders').then(res => {
        if (res.code === '200') {
          this.headerData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      this.errorMessages.employeeId = ''; // 清空错误信息
      this.errorMessages.powerName = ''; // 清空错误信息
      this.form = {}  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据

      this.errorMessages.employeeId = ''; // 清空错误信息
      this.errorMessages.powerName = ''; // 清空错误信息
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/department/update' : '/department/add',
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
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/department/delete/' + id).then(res => {
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
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/department/delete/batch', {data: this.ids}).then(res => {
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
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/department/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
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
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>

</style>
