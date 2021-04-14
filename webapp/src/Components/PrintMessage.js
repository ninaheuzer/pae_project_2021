const PrintMessage = (msg) => {
    if (!msg) return;
    let page = document.querySelector("#page");
    const div = document.createElement("div");
    div.className = "alert alert-success mt-2";
    div.id = "messageBoard";
    div.innerText = msg;
    page.appendChild(div);
    setTimeout(() => document.getElementById("messageBoard").remove(), 5000);
  };
  
  export default PrintMessage;
  