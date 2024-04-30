
<template>
  <div>
    <div class="search">
      <el-input placeholder="输入资产名称" style="width: 200px" v-model="resourcesName"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation" >
      <el-button v-if="user.role === 'ADMIN'" type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column v-if="user.role == 'ADMIN'" type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="employeeName" label="员工姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="departmentName" label="所属部门" show-overflow-tooltip></el-table-column>
        <el-table-column prop="resourcesName" label="资产名称"></el-table-column>
        <el-table-column prop="num" label="申请数量"></el-table-column>
        <el-table-column prop="time" label="申请时间"></el-table-column>
        <el-table-column prop="process" label="申请状态"></el-table-column>
        <el-table-column prop="status" label="审批结果"></el-table-column>
        <el-table-column v-if="user.role === 'ADMIN' " label="操作" width="250" align="center">
          <template v-slot="scope" >
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



    <el-dialog title="编辑" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="num" label="申请数量">
          <el-input v-model="form.num" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="process" label="审批进度">
          <el-select v-model="form.process" placeholder="请选择进度" style="width: 100%">
            <el-option label="部长审批中"  value="部长审批中"></el-option>
            <el-option label="财务部审批中" value="财务部审批中"></el-option>
            <el-option label="审批结束" value="审批结束"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="type" label="资产状态">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-if="form.process !== '审批结束'"  label="待审批" value="待审批"></el-option>
            <el-option label="审批通过"    value="审批通过"></el-option>
            <el-option label="审批不通过"   value="审批不通过"></el-option>
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
  name: "ResourcesMy",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      resourcesName: null,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ids: [],
      rules:{
        num:[
          {required: true, message: '请输入数量', trigger: 'blur'},
          { pattern: /^[1-9]\d*$/, message: '请输入正整数', trigger: 'blur' },
        ]
      },
      status: null,
      fromVisible:false,
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$request.put('/resourceapply/updateByAdmin', this.form).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.load(1)
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    del(id) {   // 单个删除
        this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
          this.$request.delete('/resourceapply/delete/' + id).then(res => {
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
    handleEdit(row) {   // 编辑数据
      if(row.process === "审批结束"){
        this.$message.warning('已审批结束状态的审批记录不可编辑')
        return
      }
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.fromVisible = true   // 打开弹窗
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
        this.$request.delete('/resourceapply/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/resourceapply/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          resourcesName: this.resourcesName,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.resourcesName = null
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
