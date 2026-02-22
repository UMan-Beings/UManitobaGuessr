<template>
  <RouterLink class="logo" to="/">
    UMG!
  </RouterLink>
</template>

<style scoped>
.logo{
  color: white;
  font-size: 80px;
  font-weight: 900;
  line-height: 1;
  text-decoration: none;
  text-shadow:
    0 2px 8px rgba(0,0,0,.6),
    0 0 1px rgba(0,0,0,.9);
  transition: color .15s ease;
}

.logo:hover{
  color:#2196f3;
}
</style>