<template>
  <!-- pin number input -->

  <div>
    <b-container class="bv-example-row">
      <h1>Kahoot</h1>
      <b-row>
        <b-col cols="5"></b-col>
        <b-col cols="2">
        <b-form>
          <b-form-group id="input-group-2" label="Game pin" label-for="input-2">
            <b-form-input
              id="input-2"
              v-model="form.gamepin"
              placeholder="Game Pin"
              required
            ></b-form-input>
          </b-form-group>

          <b-button type="submit" variant="primary">Enter</b-button>
        </b-form>
        </b-col>
        <b-col cols="5"></b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
export default {
  data() {
    return {
      name: "Participation",
      form: {
        gamepin: "",
      },
      foods: [
        { text: "Select One", value: null },
        "Carrots",
        "Beans",
        "Tomatoes",
        "Corn",
      ],
      show: true,
    };
  },
  methods: {
    onSubmit(event) {
      event.preventDefault();
      alert(JSON.stringify(this.form));
    },
    onReset(event) {
      event.preventDefault();
      // Reset our form values
      this.form.gamepin = "";
      // Trick to reset/clear native browser form validation state
      this.show = false;
      this.$nextTick(() => {
        this.show = true;
      });
    },
  },
};
</script>