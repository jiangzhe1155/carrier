<template>
  <svg :class="iconStyle" aria-hidden="true">
    <use :xlink:href="iconName"></use>
  </svg>
</template>

<script>
  export default {
    name: "icon", props: {
      iconName: {
        type: String,
        required: true
      },
      iconStyle: String
    },
  }
</script>

<style scoped>

</style>
