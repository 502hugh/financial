<template>

  <div>
    <div class="search">
      <el-input placeholder="请输入申请人名称" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>


    <div class="table">
      <el-table :data="tableData" stripe>
        <el-table-column prop="name" label="报账人" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="报账时间"></el-table-column>
        <el-table-column prop="amount" label="报账金额"></el-table-column>
        <el-table-column prop="type" label="报账类型"></el-table-column>
        <el-table-column prop="note" label="报账备注"></el-table-column>
        <el-table-column label="附件" align="center">
          <template v-slot="scope">
            <el-button type="text"  @click="openPictureDialog(scope.row)">
              <i class="el-icon-picture-outline"></i> <!-- 使用一个图片图标 -->
            </el-button>
          </template>
        </el-table-column>


        <el-table-column prop="process" label="审批进度"></el-table-column>
        <el-table-column prop="status" label="审批状态"></el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" @click="handleApply(scope.row.id)" size="mini">审批</el-button>
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


    <el-dialog title="附件详情" :visible.sync="pictureVisible" width="50%" height="50%" :close-on-click-modal="false" destroy-on-close>
      <div class="table">
        <el-table :data="pictureData" stripe>
          <el-table-column label="图片">
            <template v-slot="scope">
              <div style="display: flex; align-items: center">
                <el-image  style="width: 140px; height: 140px;" v-if="scope.row.picture"
                           :src="scope.row.picture" :preview-src-list="[scope.row.picture]"></el-image>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <el-dialog title="审批" :visible.sync="applyVisible" width="40%" height="50%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px"  ref="formRef">
        <el-form-item prop="name" label="请审批">
          <el-select v-model="status" placeholder="请选择状态" style="width: 100%">
            <el-option label="审批通过"  value="审批通过"></el-option>
            <el-option label="审批不通过" value="审批不通过"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="note" label="审批说明">
          <el-input v-model="note" autocomplete="off" placeholder="请输入审批说明"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="applyVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveChange">确 定</el-button>
      </div>
    </el-dialog>



  </div>



</template>

<script>
export default {
  name: "ReimbursementApply",
  data(){
    return{
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name:'',
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      form: {},
      rules: {
      },
      ids: [],
      pictureData:[],
      pictureVisible:false,
      applyVisible:false,
      status:null,
      note:null,
    }
  },
  created() {
    this.load(1)
  },
  methods:{


    saveChange(){
      this.form.status = this.status
      this.form.note = this.note

      this.$request.put('/reimbursementApply/update', this.form).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.load(1)
          this.applyVisible = false
          this.status = null
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    handleApply(id){
        this.form = {}
        this.form.reimbursementId = id
        this.status = null
        this.applyVisible = true
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
    load(pageNum){
      if(pageNum) this.pageNum = pageNum
      this.$request.get("/reimbursement/selectPageApply",{
        params:{
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.table = res.data?.total
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

<!-- 报账-->