<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入资产名称" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      <el-button v-if="user.role == 'ADMIN'" type="primary" plain style="margin-left: 10px" @click="downloadExcel">导出资产信息</el-button>
    </div>

    <div class="operation" v-if="user.role === 'ADMIN' || (employee.level === 2 && employee.powerName === '财务') ">
      <el-button type="primary" plain @click="handleAdd">新增</el-button>
      <el-button v-if="user.role === 'ADMIN'" type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column v-if="user.role === 'ADMIN' " type="selection" width="55" align="center"></el-table-column>
<!--        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>-->
        <el-table-column prop="img" label="资产图片" show-overflow-tooltip>
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px; border-radius: 50%" v-if="scope.row.img"
                        :src="scope.row.img" :preview-src-list="[scope.row.img]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="资产名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="type" label="资产类型" show-overflow-tooltip></el-table-column>
        <el-table-column prop="price" label="资产价格"></el-table-column>
        <el-table-column prop="num" label="剩余数量"></el-table-column>

        <el-table-column label="操作" width="250" align="center">
          <template v-slot="scope" v-if="user.role === 'ADMIN' ">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini">编辑</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>删除</el-button>
          </template>
          <template v-slot="scope" v-else-if=" (employee.level === 2 && employee.powerName === '财务')">
            <el-button plain type="primary" @click="handleEdit(scope.row)" size="mini">编辑</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>删除</el-button>
            <el-button plain type="primary" size="mini" @click="addApply(scope.row)">申请</el-button>
          </template>
          <template v-slot="scope" v-else>
            <el-button plain type="primary" size="mini" @click="addApply(scope.row)">申请</el-button>
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


    <el-dialog title="资产信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item label="资产图片">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handleAvatarSuccess"
          >
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item prop="name" label="资产名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="type" label="资产类型">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="固定资产"  value="固定资产"></el-option>
            <el-option label="不动产" value="不动产"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="price" label="资产价格">
          <el-input v-model="form.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="num" label="剩余库存">
          <el-input v-model="form.num" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>


    <el-dialog title="申请数量" :visible.sync="applyVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="applyForm" :rules="rules1" >
        <el-form-item prop="num" label="填写数量" :error="errorMessages.num" >
          <el-input v-model="applyForm.num" autocomplete="off"   @blur="handleNumChange"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="applyVisible = false, applyForm.num =null">取 消</el-button>
        <el-button type="primary" @click="apply">确 定</el-button>
      </div>
    </el-dialog>


  </div>
</template>

<script>
import employee from "@/views/manager/user/Employee.vue";

export default {
  name: "Resources",
  computed: {
    employee() {
      return employee
    }
  },
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      num: null,
      name: null,
      fromVisible: false,
      applyVisible: false,
      form: {},
      applyForm:{},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        name: [
          {required: true, message: '请输入资产名称', trigger: 'blur'},
        ],
        price: [
          {required: true, message: '请输入价格', trigger: 'blur'},
          {
            pattern: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
            message: '请输入正确的价格，最多保留两位小数',
            trigger: 'blur'
          }
        ],
        num:[
          {required: true, message: '请输入数量', trigger: 'blur'},
          { pattern: /^[1-9]\d*$/, message: '请输入正整数', trigger: 'blur' },
        ],
      },
      rules1:{
        num:[
          {required: true, message: '请输入数量', trigger: 'blur'},
          {pattern: /^[1-9]\d*$/, message: '请输入正整数', trigger: 'blur' },
        ],
      },
      errorMessages:{
        num:'',//数量出错信息
      },
      ids: [],
      employee:""
    }
  },
  mounted() {
    this.loadEmployee()
  },
  created() {
    this.load(1)
  },
  methods: {
    handleNumChange() {

      this.errorMessages.num = '';
      this.applyForm.employeeId = this.user.id
      this.applyForm.resourcesId = this.applyForm.id
      //对applyFrom.num 做数据校验
      const numPattern = /^[1-9]\d*$/;
      if (!numPattern.test(this.applyForm.num)) {
        this.errorMessages.num = '请输入正整数';
        return;
      }

      this.$request.post('/resourceapply/judgmentNum' , this.applyForm).then(res => {
        if (res.code === '200') {
          this.errorMessages.num = ''; // 清空错误信息
        } else {
          this.errorMessages.num = res.msg; // 设置错误信息
        }
      })
    },

    downloadExcel() {
      // 发送请求到后端，下载文件
      this.$request.post('/excel/resourcesOut', this.form, { responseType: 'blob' })
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
    addApply(row) {
      // 深拷贝
      this.errorMessages.num = '';
      this.applyForm = JSON.parse(JSON.stringify(row))
      this.applyForm.num = null
      this.applyVisible = true
    },
    apply() {
      // this.applyForm.num = this.form.num
      this.applyForm.employeeId = this.user.id
      this.applyForm.resourcesId = this.applyForm.id
      this.applyForm.id = null
      this.$request.post('/resourceapply/add', this.applyForm).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功，等待主管审核')
          this.applyVisible = false
          this.load(1)
          this.num = null
        } else {
          this.$message.error(res.msg)
          this.num = null
          this.applyVisible = false
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
        if (valid) {
          this.$request({
            url: this.form.id ? '/resources/update' : '/resources/add',
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
        this.$request.delete('/resources/delete/' + id).then(res => {
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
        this.$request.delete('/resources/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/resources/selectPage', {
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
    handleAvatarSuccess(response, file, fileList) {
      this.form.img = response.data
    },
  }
}
</script>

<style scoped>

</style>
