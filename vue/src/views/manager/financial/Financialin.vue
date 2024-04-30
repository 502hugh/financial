
<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入收入说明" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      <el-button v-if="user.role == 'ADMIN'" type="primary" plain style="margin-left: 10px" @click="downloadExcel">导出财务收入信息</el-button>
    </div>

    <div v-if="user.role == 'ADMIN' || employee.powerName === '财务'" class="operation">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button v-if="user.role == 'ADMIN'" type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column v-if="user.role == 'ADMIN'" type="selection" width="55" align="center"></el-table-column>
        <!--        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>-->
        <el-table-column prop="name" label ="收入说明" show-overflow-tooltip></el-table-column>
        <el-table-column prop="price"label ="收入金额" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label ="收入时间"></el-table-column>
        <el-table-column prop="type" label ="收入类型"></el-table-column>

        <el-table-column v-if="user.role == 'ADMIN' || employee.powerName === '财务'" label="操作" width="180" align="center">
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


    <el-dialog title="收入" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">


        <el-form-item prop="type" label="收入类型">
          <el-select v-model="form.type" autocomplete="off">
<!--            <el-option label="固定资产"  value="固定资产"></el-option>-->
<!--            <el-option label="不动产" value="不动产"></el-option>-->
            <el-option label="现金流" value="现金流"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="time" label="收入时间">
          <el-date-picker style="width: 100%"
                          v-model="form.time"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item prop="price" label="收入金额">
          <el-input v-model="form.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="name" label="收入说明">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="['固定资产', '不动产'].includes(form.type)" prop="resourcesNum" label="资产数量">
          <el-input v-model="form.resourcesNum" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item v-if="['固定资产', '不动产'].includes(form.type)" prop="resourcesName" label="资产名称">
          <el-input v-model="form.resourcesName" autocomplete="off"></el-input>
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
  name: "Financialin",

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
        time: [
          {required: true, message: '请选择收入时间', trigger: 'blur'},
        ],
        price: [
          {required: true, message: '请输入收入金额', trigger: 'blur'},
        ],
        resourcesName: [
          {required: true, message: '请输入资产名称', trigger: 'blur'},
        ],
        resourcesNum: [
          {required: true, message: '请输入资产名称', trigger: 'blur'},
        ],
      },
      ids: [],
      departmentData: [],
      employee:"",
    }
  },
  mounted() {
    this.loadEmployee()
  },
  created() {
    this.load(1)
  },
  methods: {
    downloadExcel() {
      // 发送请求到后端，下载文件
      this.$request.post('/excel/financialInOut', this.form, { responseType: 'blob' })
          .then(res => {
            let blob = new Blob([res], { type: 'application/xlsx' });
            let url = window.URL.createObjectURL(blob);
            const link = document.createElement('a'); //创建a标签
            link.href = url;
            link.download = '财务收入表.xlsx'; //重命名文件
            link.click();
            URL.revokeObjectURL(url);
          })
          .catch(error => {
            console.error('下载文件失败', error);
          });
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
        if (valid) {
          this.$request({
            url: this.form.id ? '/financialin/update' : '/financialin/add',
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
        this.$request.delete('/financialin/delete/' + id).then(res => {
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
        this.$request.delete('/financialin/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/financialin/selectPage', {
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