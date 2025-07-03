document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("loginForm");

    if (form) {
        form.addEventListener("submit", async function (e) {
            e.preventDefault();

            const formData = new FormData(this);

            const body = new URLSearchParams(formData);

            try {
                const response = await fetch("/login", {
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
                    alert("Invalid username or password");
                }
            } catch (error) {
                console.error("Error during login:", error);
                alert("An error occurred.");
            }
        });
    }
});
