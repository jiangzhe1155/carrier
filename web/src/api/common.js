export function formatSize(size) {
  if (size === undefined) {
    return null;
  }
  return size < 1024 ? size.toFixed(0) + " B" : size < 1048576 ? (size / 1024).toFixed(0) + " KB" : size < 1073741824 ? (size / 1024 / 1024).toFixed(1) + " MB" : (size / 1024 / 1024 / 1024).toFixed(1) + " GB";
}
