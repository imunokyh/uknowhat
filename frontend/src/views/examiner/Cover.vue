<template>
<div class="h-100">
  <body class="d-flex h-100 text-center text-white bg-dark">
    <div class="cover-container d-flex w-100 h-100 p-3 mx-auto flex-column">
      <main class="px-3">
        <h1>Cover your page.</h1>
        <p class="lead">
          Cover is a one-page template for building simple and beautiful home
          pages. Download, edit the text, and add your own fullscreen background
          photo to make it your own.
        </p>
        <p class="lead">
          <a
            href="#"
            class="btn btn-lg btn-secondary fw-bold border-white bg-white"
            >Learn more</a
          >
        </p>
      </main>

      <footer class="mt-auto text-white-50">
        <p>
          Cover template for
          <a href="https://getbootstrap.com/" class="text-white">Bootstrap</a>,
          by <a href="https://twitter.com/mdo" class="text-white">@mdo</a>.
        </p>
      </footer>
    </div>
  </body>
</div>
</template>


<script>
export default {
  data() {
    return {
      name: "TrueFalseProb",
      prob: "하나님께서 천지를 창조하셨다",
    };
  },
};
</script>

<style scoped>
/* Custom default button */
.btn-secondary,
.btn-secondary:hover,
.btn-secondary:focus {
  color: #333;
  text-shadow: none; /* Prevent inheritance from `body` */
}

/*
 * Base structure
 */

body {
  text-shadow: 0 0.05rem 0.1rem rgba(0, 0, 0, 0.5);
  box-shadow: inset 0 0 5rem rgba(0, 0, 0, 0.5);
}

.cover-container {
  max-width: 42em;
}

/*
 * Header
 */

.nav-masthead .nav-link {
  padding: 0.25rem 0;
  font-weight: 700;
  color: rgba(255, 255, 255, 0.5);
  background-color: transparent;
  border-bottom: 0.25rem solid transparent;
}

.nav-masthead .nav-link:hover,
.nav-masthead .nav-link:focus {
  border-bottom-color: rgba(255, 255, 255, 0.25);
}

.nav-masthead .nav-link + .nav-link {
  margin-left: 1rem;
}

.nav-masthead .active {
  color: #fff;
  border-bottom-color: #fff;
}

.bd-placeholder-img {
  font-size: 1.125rem;
  text-anchor: middle;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
}

@media (min-width: 768px) {
  .bd-placeholder-img-lg {
    font-size: 3.5rem;
  }
}
</style>