<template>
  <div>
<!--    <div class="card" style="padding: 15px">-->
<!--      您好，{{ user?.name }}！欢迎使用本系统-->
<!--    </div>-->

    <div v-if="user.role === 'ADMIN'" class="operation">
      <el-button type="primary"   plain  @click="noticeController"   :style="{ backgroundColor: HomeNoticeVisible ? '#99FFFF' : '#C0C4CC' }"> {{ HomeNoticeVisible ? '隐藏公告' : '显示公告' }}</el-button>
      <el-button type="primary"   plain  @click="barInController"    :style="{ backgroundColor: HomeBarInVisible ? '#99FFFF' : '#C0C4CC' }">{{HomeBarInVisible ? '隐藏收入图' : '显示收入图'  }}</el-button>
      <el-button type="primary"   plain  @click="lineInOutController" :style="{ backgroundColor: HomeLineInOutVisible ? '#99FFFF' : '#C0C4CC' }">{{ HomeLineInOutVisible ? '隐藏利润图' : '显示利润图'  }}</el-button>
      <el-button type="primary"   plain  @click="barSalaryController" :style="{ backgroundColor: HomeBarSalaryInVisible ? '#99FFFF' : '#C0C4CC' }">{{ HomeBarSalaryInVisible ? '隐藏薪资图' : '显示薪资图'  }}</el-button>
      <el-button type="primary"   plain  @click="pieController"      :style=" { backgroundColor: HomePieInVisible ? '#99FFFF' : '#C0C4CC' }"> {{ HomePieInVisible ? '隐藏支出图' : '显示支出图'  }}</el-button>
    </div>

<!--    //公告部分-->
    <div v-if="HomeNoticeVisible " style="display: flex; margin: 10px 0">
      <div style="width: 100%;" class="card">
        <div style="margin-bottom: 30px; font-size: 20px; font-weight: bold">公告列表</div>
        <div>
          <el-timeline  reverse slot="reference">
            <el-timeline-item v-for="item in notices" :key="item.id" :timestamp="item.time">
              <el-popover
                  placement="right"
                  width="200"
                  trigger="hover"
                  :content="item.content">
                <span slot="reference">{{ item.title }}</span>
              </el-popover>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </div>


<!--    图部分-->
<!--    <div v-if="user.role === 'USER'">-->
      <div style="display: flex">
        <div  v-if="HomeBarInVisible "  id="bar" style="height: 400px; flex: 1" class="card"></div>
        <div  v-if="HomeLineInOutVisible "  id="line" style="height: 400px; flex: 1" class="card"></div>
      </div>
      <div style="margin-top: 10px; display: flex">
        <div  v-if="HomeBarSalaryInVisible "  style="height: 450px; flex: 1;" class="card">
          <el-select v-model="month" style="width: 50%" @change="getSalaryMonth(month)">
            <el-option v-for="item in monthData" :label="item.year" :value="item.year"></el-option>
          </el-select>
          <div id="salaryBar" style="height: 450px; padding-top: 15px"></div>
        </div>
        <div  v-if="HomePieInVisible "  id="pie" style="height: 450px; flex: 1" class="card"></div>
      </div>
<!--    </div>-->
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

let barSalaryOptions = {
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
      type: 'bar',
      itemStyle: {
        normal: {
          color:function(){return "#"+Math.floor(Math.random()*(256*256*256-1)).toString(16);}
        },
      },
    }
  ]
}


let barOptions = {
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
      type: 'bar',
      itemStyle: {
        normal: {
          color:function(){return "#"+Math.floor(Math.random()*(256*256*256-1)).toString(16);}
        },
      },
    }
  ]
}

export default {
  name: 'Home',
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      notices: [],
      month: null,
      monthData: [],
      VisibleController:{
      },
      HomeNoticeVisible:null,
      HomeBarInVisible:null,
      HomeLineInOutVisible:null,
      HomeBarSalaryInVisible:null,
      HomePieInVisible:null,
    }
  },
  mounted() {

    setTimeout(() => {
      this.load()
    }, 500); // 1000 毫秒延时
  },
  created() {
    this.$request.get('/notice/selectAll').then(res => {
      this.notices = res.data || []
    })
    this.loadVisibleController()
  },
  methods:{
    load(){
      if(this.HomeBarInVisible){
        this.getFinancialBar()
      }
      if(this.HomeLineInOutVisible){
        this.getFinancialLine()
      }
      if(this.HomeBarSalaryInVisible){
        this.getMonthData()
        this.getSalaryMonth()
      }
      if(this.HomePieInVisible){
        this.getFinancialPie() //财务支出饼图
      }
    },
    noticeController(){
      this.HomeNoticeVisible =  !this.HomeNoticeVisible
      this.VisibleController.homeNoticeVisible = this.HomeNoticeVisible
      this.visibleUpdate()
    },
    barInController(){
      this.HomeBarInVisible = !this.HomeBarInVisible
      this.VisibleController.homeBarInVisible = this.HomeBarInVisible
      this.visibleUpdate()
      if(this.HomeBarInVisible){
        this.getFinancialBar()
      }
      setTimeout(() => {
        window.location.reload();
      }, 50);
    },
    lineInOutController(){
      this.HomeLineInOutVisible = !this.HomeLineInOutVisible

      this.VisibleController.homeLineInOutVisible = this.HomeLineInOutVisible
      this.visibleUpdate()
      if(this.HomeLineInOutVisible){
        this.getFinancialLine()
      }
      setTimeout(() => {
        window.location.reload();
      }, 50);
    },
    barSalaryController(){
      this.HomeBarSalaryInVisible = !this.HomeBarSalaryInVisible
      this.VisibleController.homeBarSalaryInVisible = this.HomeBarSalaryInVisible
      this.visibleUpdate()
      if(this.HomeBarSalaryInVisible){
        this.getMonthData()
        this.getSalaryMonth()
      }
      setTimeout(() => {
        window.location.reload();
      }, 50);
    },
    pieController(){
      this.HomePieInVisible  = !this.HomePieInVisible
      this.VisibleController.homePieInVisible = this.HomePieInVisible
      this.visibleUpdate()
      if(this.HomePieInVisible){
        this.getFinancialPie() //财务支出饼图
      }
      setTimeout(() => {
        window.location.reload();
      }, 50);
    },
    visibleUpdate(){
      this.$request.put('/visible/update', this.VisibleController).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadVisibleController(){
      //获取当前员工用户的信息
      this.$request.get('/visible/select').then(res => {
        if (res.code === '200') {
          this.VisibleController = res.data
         this.HomeNoticeVisible =  this.VisibleController.homeNoticeVisible
         this.HomeBarInVisible  =  this.VisibleController.homeBarInVisible
         this.HomeLineInOutVisible = this.VisibleController.homeLineInOutVisible
         this.HomeBarSalaryInVisible =this.VisibleController.homeBarSalaryInVisible
         this.HomePieInVisible = this.VisibleController.homePieInVisible

        } else {
          this.$message.error(res.msg)
        }
      })
    },



    getFinancialPie(){
      this.$request.get('/financialout/getPie').then(res => {
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
    getFinancialLine() {
      this.$request.get('/financialout/getLine').then(res => {
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
    getMonthData() {
      this.$request.get('/salary/getMonth').then(res => {
        if (res.code === '200') {
          this.monthData = res.data
          this.month = this.monthData[0].year
          this.getSalaryMonth(this.month)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getSalaryMonth(month) {
      this.$request.get('/salary/getSalary/' + month).then(res => {
        if (res.code === '200') {
          let chartDom = document.getElementById('salaryBar');
          let myChart = echarts.init(chartDom);
          barSalaryOptions.title.text = res.data.text
          barSalaryOptions.title.subtext = res.data.subtext
          barSalaryOptions.xAxis.data = res.data.xAxis
          barSalaryOptions.series[0].data = res.data.yAxis
          myChart.setOption(barSalaryOptions)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getFinancialBar() {
      this.$request.get('/financialout/getBar').then(res => {
        if (res.code === '200') {
          let chartDom = document.getElementById('bar');
          let myChart = echarts.init(chartDom);
          barOptions.title.text = res.data.text
          barOptions.title.subtext = res.data.subtext
          barOptions.xAxis.data = res.data.xAxis
          barOptions.series[0].data = res.data.yAxis
          myChart.setOption(barOptions)
        }
      })
    }
  }
}
</script>
