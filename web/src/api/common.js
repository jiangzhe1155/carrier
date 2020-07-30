function formatSize(size) {
  if (size === undefined) {
    return null;
  }
  return size < 1024 ? size.toFixed(0) + " B" : size < 1048576 ? (size / 1024).toFixed(0) + " KB" : size < 1073741824 ? (size / 1024 / 1024).toFixed(1) + " MB" : (size / 1024 / 1024 / 1024).toFixed(1) + " GB";
}

function isFolder(type) {
  return type === 0;
}

function extName(fileName) {
  let lastIndexOf = fileName.lastIndexOf('.');
  if (lastIndexOf === -1) {
    return "";
  }
  return fileName.substr(lastIndexOf + 1)
}

function getIcon(type, fileName) {
  if (isFolder(type)) {
    return "#el-icon-alifolder";
  }
  let ext = extName(fileName);
  if (!iconTypeMap.has(ext)) {
    return '#el-icon-alifile_cloud-copy';
  }
  return iconTypeMap.get(ext);
}


let iconTypeMap = new Map()
  .set('pdf', '#el-icon-alifile_pdf')
  .set('txt', '#el-icon-alifile_txt')
  .set('zip', '#el-icon-alifile_zip')
  .set('csv', '#el-icon-alifile_excel')
  .set('mp3', '#el-icon-alifile_music')
  .set('jpg', '#el-icon-alifile_img')
  .set('cad', '#el-icon-alifile_cad')
  .set('word', '#el-icon-alifile_word')
  .set('mp4', '#el-icon-alifile_video');

export default {
  formatSize,
  isFolder,
  getIcon,
  iconTypeMap
}
