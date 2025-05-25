let stompClient = null;

const connectWebSocket = () => {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, (frame) => {
        console.log('Connected: ' + frame);

        const currentUserId = parseInt(document.getElementById('senderId').value, 10);

        stompClient.subscribe(`/topic/messages/${currentUserId}`, (message) => {
            const receivedMessage = JSON.parse(message.body);
            const currentReceiverId = parseInt(document.getElementById('receiverId').value, 10);

            // Display message only if it belongs to the current conversation
            if (
                (receivedMessage.senderId === currentUserId && receivedMessage.receiverId === currentReceiverId) ||
                (receivedMessage.senderId === currentReceiverId && receivedMessage.receiverId === currentUserId)
            ) {
                displayMessage(receivedMessage);
            }
        });
    });
};

window.onload = connectWebSocket;



