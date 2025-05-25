document.getElementById("togglePassword").addEventListener("click", function () {
           const passwordField = document.getElementById("password");
           const type = passwordField.getAttribute("type") === "password" ? "text" : "password";
           passwordField.setAttribute("type", type);
           this.textContent = type === "password" ? "👁️" : "🔒";
       });
       
        function cancelForm() {
       if (confirm("Are you sure you want to cancel? Unsaved changes will be lost.")) {
           window.location.href = "/sellerdashboard";
       }
	   }