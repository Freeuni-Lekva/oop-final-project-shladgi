document.addEventListener("DOMContentLoaded", function () {
    const button = document.getElementById("button");
    const name = button.getAttribute("name");
    const form = document.getElementById(name + "Form");


    if (form) {
        form.addEventListener("submit", async function (e) {
            e.preventDefault();

            const formData = new FormData(this);

            const body = new URLSearchParams(formData);

            try {
                const response = await fetch("/" + name, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body: body
                });

                const result = await response.json();

                if (result.success) {
                    window.location.href = "/home";
                } else {
                    const errorDiv = document.getElementById("registerError");
                    if (result.error === "username_taken") {
                        errorDiv.textContent = "Username already used. Please choose another.";
                    }else if (result.error === "wrong_username_or_password"){
                        errorDiv.textContent = "Either username or password is incorrect.";
                    } else {
                        errorDiv.textContent = "Registration failed. Try again.";
                    }
                }
            } catch (error) {
                console.error("Error during login:", error);
                alert("An error occurred.");
            }
        });
    }
});
