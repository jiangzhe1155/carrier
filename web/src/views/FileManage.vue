<template>
  <div>


    <el-table :data="files" stripe @selection-change="handleSelectionChange">
      <el-table-column type="selection"></el-table-column>
      <el-table-column label="文件名">
        <template slot-scope="scope">
          <div style="display: flex;flex-direction: row;align-items: center;">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#el-icon-alifolder"/>
            </svg>
            <el-link :underline="false" v-if="!scope.row.editable" style="padding-left: 5px">
              {{scope.row.fileName}}
            </el-link>
            <div v-else>
              <el-input clearable v-model="fileNameInput" ref="editInput" size="mini"></el-input>
              <el-button-group>
                <el-button icon="el-icon-check" size="mini"
                           @click="onEditConfirm(scope.row,scope.$index)"></el-button>
                <el-button icon="el-icon-close" size="mini" @click="onClickClose(scope.row,scope.$index)"></el-button>
              </el-button-group>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="size" label="大小">
        <template slot-scope="scope">{{formatSize(scope.row.size)}}</template>
      </el-table-column>
      <el-table-column prop="updateTime" label="最后修改时间"></el-table-column>
    </el-table>
  </div>

</template>
<script>
  export default {
    data() {
      return {
        params: {},
        files: [],
        fileSelections: [],
        fileNameInput: ""
      }
    },
    created() {
      this.params = this.$route.query;
      this.getFileList(this.params);
    },
    methods: {
      getFileList(params) {
        this.post("list", params).then(data => {
          this.files = data.data.records;
        });
      },
      handleSelectionChange(val) {
        this.fileSelections = val;
      },
      getIcon(file) {
        if (file.type === 0) {
          return "el-icon-alifolder";
        }


      }
    },
    watch: {
      '$route': function () {
        this.params = this.$route.query;
      }
    }
  }
</script>

<style lang="less">
  .icon {
    width: 2em;
    height: 2em;
    fill: currentColor;
    overflow: hidden;
  }
</style>
