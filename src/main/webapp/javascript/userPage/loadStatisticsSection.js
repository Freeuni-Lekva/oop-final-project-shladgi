document.addEventListener("DOMContentLoaded", ()=>{
        const button = document.getElementById("statisticsMenuItem");
        button.addEventListener("click", async (e) => {
            e.preventDefault();
            document.getElementById("user").style.display = "none";
            document.getElementById("statistics").style.display = "block";
            document.getElementById("friends").style.display = "none";

        });
    }
);