
function onLoad() {
    var wsUri = "ws://192.168.1.229:8080/chat";
    websocket = new WebSocket(wsUri);
    websocket.onopen = function(evt) { onOpen(evt) };
    websocket.onclose = function(evt) { onClose(evt) };
    websocket.onmessage = function(evt) { onMessage(evt) };
    websocket.onerror = function(evt) { onError(evt) };
}

function onOpen(evt) {
    state.className = "success";
    state.innerHTML = "Connected to server";
}

function onClose(evt) {
    state.className = "fail";
    state.innerHTML = "Not connected";
    connected.innerHTML = "0";
}

function onMessage(evt) {
    var message = evt.data;

    if (message.startsWith("log:")) {
        message = message.slice("log:".length);
        log.innerHTML = log.innerHTML + "<li class = \"message\">" + message + "</li>";
        log.scrollTop = log.scrollHeight;
    }else if (message.startsWith("connected:")) {
        message = message.slice("connected:".length);
        connected.innerHTML = message;
    }
}

function onError(evt) {
    state.className = "fail";
    state.innerHTML = "Communication error";
}

function addMessage() {
    var message = chat.value;
    chat.value = "";
    websocket.send(message);
}
