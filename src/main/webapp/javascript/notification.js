import { loadSessionValue } from "./getSessionInfo.js";


let noteInterval =0;
let chalInterval = 0;
let userid = 0;





const getNotes = function (){
noteInterval++;
console.log(noteInterval);
    fetch("getNotes",
        {method : "POST",
            headers :{
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body : new URLSearchParams({
                userid :  userid,
                interval : noteInterval
            })
        }
    ).then(res => res.json())
        .then(data => {
           if(data.success){

               let notes =  document.getElementById("notes");
               data.info.forEach(n=> {
                  notes.innerHTML+= `<div class="note ${n.viewed ? "" : "viewed"}">
                                    <a href="user?userName=${n.senderName}">${n.senderName}</a><span>${new Date(n.createDate).toLocaleDateString("en-GB")}</span>
                                    <p>${n.text}</p></div>`;
               })
               if (data.info.length < 10) {
                   document.getElementById("smn").style.display = "none";
               }
           }
            }
        ).catch(err=> {
            console.log(err);
    });
}

const getChallenges = function (){
    chalInterval++;
    fetch("getChallanges",
        {method : "POST",
            headers :{
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body : new URLSearchParams({
                userid :  userid,
                interval : chalInterval
            })
        }
    ).then(res => res.json())
        .then(data => {
            if(data.success){
                let notes =  document.getElementById("challenges");
                data.info.forEach(c=> {
                    notes.innerHTML+= `<div class="note ${c.viewed ? "" : "viewed"}">
                                    <a href="user?userName=${c.senderName}">${c.senderName}</a><span>${new Date(c.createDate).toLocaleDateString("en-GB")}</span>
                                    <p>my scrore ${c.score}</p>
                                    <a href="/startQuiz?id=${c.quizId}"> ${c.quizTitle}</a>

                                    </div>`;
                });

                if (data.info.length < 10) {
                    document.getElementById("smc").style.display = "none";
                }
            }

            }

        ).catch(err=> {
        console.log(err);
    });
}
document.addEventListener("DOMContentLoaded", async function () {
    userid = await loadSessionValue("userid");
    getNotes();
    getChallenges();


    document.getElementById("smn").addEventListener("click", () => {
        getNotes();
    });
    document.getElementById("smc").addEventListener("click", () => {
        getChallenges();
    })

});
