function findGame(){
    document.getElementById("findGame").style.display = "none";
    document.getElementById("findingGame").style.display = "";
    document.getElementById("cancel").style.display = "";
}

function findingGame(){
    document.getElementById("findingGame").style.display = "none";
    document.getElementById("cancel").style.display = "none";
    document.getElementById("gameIng").style.display = "";
}

function cancel(){
    document.getElementById("findGame").style.display = "";
    document.getElementById("findingGame").style.display = "none";
    document.getElementById("cancel").style.display = "none";
    document.getElementById("gameIng").style.display = "none";
}

function gameIng(){
    alert('게임 진행 중입니다.');
    return false;
}


