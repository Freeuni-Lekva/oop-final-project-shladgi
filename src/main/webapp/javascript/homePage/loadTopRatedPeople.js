import {getUserDiv} from "../userDivSmallJS.js";

document.addEventListener("DOMContentLoaded", async () => {
    const topRatedContainer = document.getElementById("top-rated-people-container");
    try {
        const response = await fetch("/get-top-rated-people", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            // body: `username=${encodeURIComponent("")}`
        });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const list = await response.json();

        topRatedContainer.innerHTML = "";
        for(const name of list){
            const div = await getUserDiv(name);
            topRatedContainer.appendChild(div);
        }
    } catch (error) {
        console.error("Failed to load top rated people:", error);
    }
});
