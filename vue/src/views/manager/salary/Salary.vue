<template>
  <div>
    <div class="search">
      <el-input placeholder="请输年月：例如 2023-02" style="width: 200px" v-model="year"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      <el-button type="primary" plain style="margin-left: 10px" @click="downloadExcel">导出薪资表</el-button>

    </div>

    <div v-if="user.role == 'ADMIN'" class="operation">
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column v-if="user.role == 'ADMIN'" type="selection" width="55" align="center"></el-table-column>
<!--        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>-->
        <el-table-column prop="employeeName" label="员工姓名" show-overflow-tooltip></el-table-column>
        <el-table-column prop="departmentName" label="所属部门" show-overflow-tooltip></el-table-column>
        <el-table-column prop="year" label="年月"></el-table-column>
        <el-table-column prop="time" label="发放时间"></el-table-column>
        <el-table-column prop="price" label="发放薪资"></el-table-column>
        <el-table-column prop="note" label="备注说明"></el-table-column>

        <el-table-column v-if="user.role == 'ADMIN'" label="操作" width="180" align="center">
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


    <el-dialog title="薪资" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="employeeId" label="选择员工">
          <el-select v-model="form.employeeId" placeholder="请选择员工" style="width: 100%">
            <el-option v-for="item in employeeData" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="year" label="发放年月">
          <el-date-picker style="width: 100%"
                          v-model="form.year"
                          type="month"
                          value-format="yyyy-MM"
                          placeholder="选择月">
          </el-date-picker>
        </el-form-item>
        <el-form-item prop="time" label="发放时间">
          <el-date-picker style="width: 100%"
                          v-model="form.time"
                          type="datetime"
                          value-format="yyyy-MM-dd HH:mm:ss"
                          placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item prop="price" label="发放薪资">
          <el-input v-model="form.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="note" label="备注说明">
          <el-input v-model="form.note" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>


    <div style="display: flex">

      <div id="pie" style="height: 400px; flex: 1" class="card"></div>
      <div id="line" style="height: 400px; flex: 1" class="card"></div>

    </div>


  </div>
</template>

<script>
import * as echarts from "echarts";
let pieOptions = {
      title: {
        text: '', // 主标题
        subtext: '', // 副标题
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '', // 鼠标移上去显示内容
          type: 'pie',
          radius: '50%',
          center: ['50%', '60%'],
          data: [
            {value: 1048, name: '瑞幸咖啡'}, // 示例数据：name表示维度，value表示对应的值
          ]
        }
      ]
    }

let lineOptions = {
  title: {
    text: '', // 主标题
    subtext: '', // 副标题
    left: 'center'
  },
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'] // 示例数据：统计的维度（横坐标）
  },
  yAxis: {
    type: 'value'
  },
  tooltip: {
    trigger: 'item'
  },
  series: [
    {
      data: [120, 200, 150, 80, 70, 110, 130], // 示例数据：横坐标维度对应的值（纵坐标）
      type: 'line',
    }
  ]
}

export default {
  name: "Department",


  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 5,  // 每页显示的个数
      total: 0,
      year: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        employeeId: [
          {required: true, message: '请选择员工', trigger: 'blur'},
        ],
        year: [
          {required: true, message: '请选择发放年月', trigger: 'blur'},
        ],
        price: [
          {required: true, message: '请输入发放薪资', trigger: 'blur'},
          {
            pattern: /^(0|[1-9]\d*)(\.\d{1,2})?$/,
            message: '请输入正确的薪资，最多保留两位小数',
            trigger: 'blur'
          }

        ]
      },
      ids: [],
      employeeData: [],
      employeePower:""
    }
  },

  mounted() {
    this.loadEmployeePowername()
  },
  created() {
    this.load(1)
    this.loadEmployee()
    this.getSalaryPie()
    this.getSalaryLine()
  },
  methods: {


    downloadExcel() {
      // 发送请求到后端，下载文件
      this.$request.post('/excel/salaryOut', this.form, { responseType: 'blob' })
          .then(res => {
            let blob = new Blob([res], { type: 'application/xlsx' });
            let url = window.URL.createObjectURL(blob);
            const link = document.createElement('a'); //创建a标签
            link.href = url;
            link.download = '薪资表.xlsx'; //重命名文件
            link.click();
            URL.revokeObjectURL(url);
          })
          .catch(error => {
            console.error('下载文件失败', error);
          });
    },


    loadEmployeePowername(){
      //获取当前员工用户的信息
      this.$request.get('/employee/selectPowerName').then(res => {
        if (res.code === '200') {
          this.employeePower = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    } ,

    getSalaryPie(){
      this.$request.get('/salary/getPie').then(res => {
        if (res.code === '200') {
          let chartDom = document.getElementById('pie');
          let myChart = echarts.init(chartDom);
          pieOptions.title.text = res.data.text
          pieOptions.title.subtext = res.data.subtext
          pieOptions.series[0].name = res.data.name
          pieOptions.series[0].data = res.data.data
          myChart.setOption(pieOptions);
        } else {
          this.$message.error(res.msg)
        }
      })
    },

    getSalaryLine(){
      this.$request.get('/salary/getLine').then(res => {
        if (res.code === '200') {
          let chartDom = document.getElementById('line');
          let myChart = echarts.init(chartDom);
          lineOptions.title.text = res.data.text
          lineOptions.title.subtext = res.data.subtext
          lineOptions.xAxis.data = res.data.xAxis
          lineOptions.series[0].data = res.data.yAxis
          myChart.setOption(lineOptions)
        } else {
          this.$message.error(res.msg)
        }
      })
    },




    loadEmployee() {
      this.$request.get('/employee/selectAll').then(res => {
        if (res.code === '200') {
          this.employeeData = res.data
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
        if (valid) {
          this.$request({
            url: this.form.id ? '/salary/update' : '/salary/add',
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
        this.$request.delete('/salary/delete/' + id).then(res => {
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
        this.$request.delete('/salary/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/salary/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          year: this.year,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.year = null
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
