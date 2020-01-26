var node = document.createElement("div");

window.onload = function() {
    node.className = "loader";
    node.innerHTML = 
        "   <div></div>" + 
        "   <div></div>" + 
        "   <div></div>" + 
        "   <div></div>" + 
        "   <div></div>" + 
        "   <div></div>";
    document.body.appendChild(node);
}

var stack = 0;

function showLoader() {
    if (stack == 0) {
        forceLoaderShow();
    }
    stack++;
}

function hideLoader() {
    stack--;
    if (stack == 0) {
        forceLoaderHide();
    }
}

function forceLoaderShow() {
    node.style.display = "flex";
    $('<div class="modal-backdrop"></div>').appendTo(document.body).css("opacity", "0.8");
    console.log('loader shown');
}
function forceLoaderHide() {
    node.style.display = "none";
    $(".modal-backdrop").remove();
    console.log('loader hidden');
}