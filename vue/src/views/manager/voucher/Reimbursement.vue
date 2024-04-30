

<template>

  <div>
    <div class="search">
      <el-input placeholder="请输入申请人名称" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px"  size="mini" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" size="mini" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button  v-if =" user.role == 'USER' " size="mini" type="primary" plain @click="handleAdd(user)">新增</el-button>
      <el-button type="danger" size="mini" plain @click="delBatch">批量删除</el-button>
    </div>


    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="name" label="报账人" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="报账时间"></el-table-column>
        <el-table-column prop="amount" label="报账金额"></el-table-column>
        <el-table-column prop="type" label="报账类型"></el-table-column>
        <el-table-column label="附件"  >
          <template v-slot="scope">
            <el-button type="text" @click="openPictureDialog(scope.row)">
              <i class="el-icon-picture-outline"></i> <!-- 使用一个图片图标 -->
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="process" label="审批进度"></el-table-column>
        <el-table-column prop="status" label="审批状态"></el-table-column>

        <el-table-column v-if =" user.role === 'ADMIN' "label="报账状态" width="250" align="center">
          <template  v-slot="scope" >
            <span v-if="scope.row.submitted === 0" style="color: black;">未提交</span>
            <span v-if="scope.row.submitted === 1" style="color: red;">已提交申请</span>
            <span v-if="scope.row.submitted === 2" style="color: green;">审批结束</span>
          </template>
        </el-table-column>
        <el-table-column prop="note" label="备注"></el-table-column>


        <el-table-column label="操作" width="250" align="center">
          <template v-if =" user.role === 'USER' " v-slot="scope" >
            <el-button v-if="scope.row.submitted === 0"plain type="primary" @click="handleEdit(scope.row)" width="50" size="mini">编辑</el-button>
            <el-button v-if="scope.row.submitted === 0" plain type="primary" @click="submit(scope.row)" width="50" size="mini">提交</el-button>
            <el-button v-if="scope.row.submitted === 0"plain type="danger" size="mini" width="50" @click=del(scope.row.id)>删除</el-button>
            <span v-if="scope.row.submitted === 1" style="color: red;">已提交申请</span>
            <span v-if="scope.row.submitted === 2" style="color: green;">审批结束</span>
          </template>
          <template v-else-if =" user.role === 'ADMIN' " v-slot="scope" >
            <el-button  plain type="primary" @click="handleEditByAdimn(scope.row)" width="50" size="mini">编辑</el-button>
            <el-button  plain type="danger" size="mini" width="50" @click=del(scope.row.id)>删除</el-button>
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

    <el-dialog title="附件详情" :visible.sync="pictureVisible" width="50%" height="50%":close-on-click-modal="false" destroy-on-close>
      <div class="operation">
        <el-button type="primary" plain @click="handleAddPicture(pictureDialogUsernameId)">新增</el-button>
<!--        <el-button type="danger" plain @click="delBatch">批量删除</el-button>-->
      </div>
      <div class="table">
        <el-table :data="pictureData" stripe>
<!--          <el-table-column prop="id" width="180" label="序号"></el-table-column>-->
          <el-table-column label="图片">
            <template v-slot="scope">
              <div style="display: flex; align-items: center">
                <el-image  style="width: 140px; height: 140px;" v-if="scope.row.picture"
                          :src="scope.row.picture" :preview-src-list="[scope.row.picture]"></el-image>
              </div>
            </template>
          </el-table-column>
            <el-table-column label="操作" width="300" align="center">
              <template v-slot="scope" >
                <el-button plain type="primary" @click="handlePictureEdit(scope.row)" size="mini">替换</el-button>
                <el-button plain type="danger" size="mini" @click=delPicture(scope.row.id)>删除</el-button>
              </template>
            </el-table-column>
        </el-table>
      </div>
    </el-dialog>


    <el-dialog title="附件" :visible.sync="pictureUpdate" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px"  ref="formRef">
        <el-form-item label="附件">
          <el-upload
              class="reimbursement-uploader"
              :action="$baseUrl + '/files/uploads'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handlePictureSuccess"
              :on-remove="handlePictureRemove"
              name="fileList"
          >
            <el-button type="primary">上传</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="pictureUpdate = false">取 消</el-button>
        <el-button type="primary" @click="saveChange">确 定</el-button>
      </div>
    </el-dialog>


    <el-dialog title="报账" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
        <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules1" ref="formRef">
          <el-form-item label="类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
                <el-option label="差旅报账"   value="差旅报账"></el-option>
                <el-option label="活动报账"   value="活动报账"></el-option>
              </el-select>
          </el-form-item>
          <el-form-item label="金额" prop="amount">
            <el-input v-model="form.amount" placeholder="金额"></el-input>
          </el-form-item>

          <el-form-item label="备注" prop="note">
            <el-input v-model="form.note" placeholder="备注"></el-input>
          </el-form-item>
          <el-form-item label="凭证">
            <el-upload
                class="avatar-uploader"
                :action="$baseUrl + '/files/uploads'"
                :headers="{ token: user.token }"
                list-type="picture"
                :on-success="handlePictureSuccess"
                :on-remove="handlePictureRemove"
                :multiple="true"
                name="fileList"
            >
              <el-button type="primary">上传凭证</el-button>
            </el-upload>
          </el-form-item>
        </el-form>

        <div slot="footer" class="dialog-footer">
          <el-button @click="fromVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
    </el-dialog>


    <el-dialog title="管理员编辑" :visible.sync="fromVisibleByAdmin" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules2" ref="formRef">
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="差旅报账"   value="差旅报账"></el-option>
            <el-option label="活动报账"   value="活动报账"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="process" label="审批进度">
          <el-select v-model="form.process" placeholder="请选择进度" style="width: 100%">
            <el-option label="财务部门审批中"  value="部长审批中"></el-option>
            <el-option label="财务人员审批中" value="财务部审批中"></el-option>
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
        <el-button type="primary" @click="saveByAdmin">确 定</el-button>
      </div>
    </el-dialog>



  </div>



</template>

<script>
export default {
  name: "Reimbursement",
  data(){
    return{
      tableData: [],  // 所有的数据
      pictureData: [],//图片数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name:null,
      fromVisible: false,
      pictureVisible: false,
      pictureUpdate: false,
      pictureDialogUsernameId: 0,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      form: {},
      rules1: {
        type: [
          { required: true, message: '请选择类型', trigger: 'blur' },
        ],
        amount: [
          { required: true, message: '请输入金额', trigger: 'blur' },
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
      ids: [],
      fromVisibleByAdmin:false,
    }
  },
  created() {
    this.load(1)
  },
  methods:{

    delPicture(id){
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response =>{
        this.$request.delete("/voucherPicture/delete/"+id).then(res=>{
          if(res.code === "200"){
            this.$message.success('操作成功')
            this.load(1)

          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
        this.pictureVisible = false
      })

    },


    submit(row){
      this.$confirm('您确定提交吗？', '确认提交', {type: "warning"}).then(response =>{
        this.form = JSON.parse(JSON.stringify(row))//赋值先
        this.$request.put("/reimbursement/submitted",this.form).then(res=>{
          if(res.code === "200"){
            this.$message.success('提交成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      })

    },
    saveChange(){
      this.$refs.formRef.validate((valid) =>{
        if(valid){
          this.$request({
            url: this.form.id ? '/voucherPicture/update' : '/voucherPicture/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('操作成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }

            this.pictureUpdate = false
            this.pictureVisible = false
          })
        }
      })

    },
    saveByAdmin(){
      this.$request.put('/reimbursement/updateByAdmin', this.form).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.load(1)
          this.fromVisibleByAdmin = false
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    save(){
      this.$refs.formRef.validate((valid) =>{
        if(valid){
          this.$request({
            url: this.form.id ? '/reimbursement/update' : '/reimbursement/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('操作成功')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    del(id){
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response =>{
        this.$request.delete("/reimbursement/delete/"+id).then(res=>{
          if(res.code === "200"){
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      })
    },

    handlePictureEdit(row){
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.form.pictures = []
      this.pictureVisible = false
      this.pictureUpdate = true
    },

    handleEditByAdimn(row){
      if(row.submitted === 2){
        this.$message.warning('无法对审批结束的数据进行编辑')
        return
      }
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.form.pictures = []
      this.fromVisibleByAdmin = true   // 打开弹窗
    },
    handleEdit(row){
      if(row.submitted === 2){
        this.$message.warning('无法对审批结束的数据进行编辑')
        return
      }
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      this.form.pictures = []
      this.fromVisible = true   // 打开弹窗
    },
    openPictureDialog(row){
      if(row.id) this.id = row.id
      this.$request.get("/reimbursement/selectPictureByReimbursementId/" + row.id).then(res =>{
        if(res.code === "200"){
          this.pictureData = res.data?.list
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
      this.pictureVisible = true   // 打开弹窗
      this.pictureDialogUsernameId = row.id // 把报账id赋值给下个表
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch(){
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除',{type: "warning"}).then(response => {
        this.$request.delete("/reimbursement/delete/batch",{data: this.ids}).then(res =>{
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      })
    },

    handleAddPicture(pictureDialogUsernameId){
      this.form = {
        pictures: [],
        //账的id唯一
        reimbursementId:pictureDialogUsernameId,
      }  // 新增数据的时候清空数据
      this.form.pictureDialogUsernameId = pictureDialogUsernameId
      this.pictureVisible = false
      this.pictureUpdate = true // 打开弹窗
    },
    handleAdd(){
      this.form = {
        pictures: [],
      }  // 新增数据的时候清空数据
      this.fromVisible = true   // 打开弹窗
    },
    load(pageNum){
      if(pageNum) this.pageNum = pageNum
      this.$request.get("/reimbursement/selectPage",{
        params:{
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
    handlePictureSuccess(response, file,fileList) {
      //单文件
      this.form.picture = response.data.filePath + "-" + response.data.uploadedFiles[0]
      console.log(this.form.picture)
      //多文件
      this.form.pictures.push(this.form.picture)
      console.log(this.form.pictures)

    },
    handlePictureRemove(response, file,fileList){

    }
  }
}
</script>

<style scoped>


</style>

<!-- 报账-->