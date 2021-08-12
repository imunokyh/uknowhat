<template>
  <div id="app">
    <div>
      <b-button>Button</b-button>
      <b-button variant="danger">Button</b-button>
      <b-button variant="success">Button</b-button>
      <b-button variant="outline-primary">Button</b-button>

      <notifications group="notifyApp" position="bottom center" />
      <b-button @click="onClick">Noti Button</b-button>
    </div>
    <div>
      <b-card
        title="Card Title"
        img-src="https://picsum.photos/600/300/?image=25"
        img-alt="Image"
        img-top
        tag="article"
        style="max-width: 20rem"
        class="mb-2"
      >
        <b-card-text>
          Some quick example text to build on the card title and make up the
          bulk of the card's content.
        </b-card-text>

        <b-button href="#" variant="primary">Go somewhere</b-button>
      </b-card>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      name: "Basic",
    };
  },
  methods: {
    onClick() {
      this.$notify({
        group: "notifyApp",
        type: "default",
        duration: 5000,
        title: "clicked!",
        text: "hello notifications",
      });
    },
  },
};
</script>

<style>
#app {
  margin: 20px;
}
</style>