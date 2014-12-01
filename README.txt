<button onclick="startTimer();">startTimer</button>
<button onclick="stopTimer();">stopTimer</button>
<script type="text/javascript">  
  function startTimer() { 
    HelloCallback.start(
      function(msgfromnative){
        console.log(msgfromnative);
      },
      function(msgfromnative){
        console.log(msgfromnative);
      });
      console.log("in function startTimer.");
  }
  function stopTimer() {
    HelloCallback.stop(
      function(msgfromnative){
        console.log(msgfromnative);
      },
      function(msgfromnative){
        console.log(msgfromnative);
      });
      console.log("in function stopTimer.");
  };
</script>
