import React from 'react';
import 'webrtc-adapter';
// import { w3cwebsocket as W3CWebSocket } from "websocket";

export default class VideoStreamer extends React.Component {


    constructor(props) {
        super(props);
        this.videoRef = null;
        this.ws = null;
        this.state = {
            cameraAvailable: false
        }
    }

    componentDidMount() {
        // Request access to browser camera and initialize video object
        navigator.mediaDevices
            .getUserMedia({video: true})
            .then((stream) => {
                this.videoRef.srcObject = stream;

                this.setState({cameraAvailable: true})
                // this.videoRef.start();
            })
            .catch(e => {
                console.error(e.name + ": " + e.message)
            });

        // Connect to websocket
        const WS_URL = "ws://localhost:3000/ws";
        const FPS = 3;

        this.ws = new WebSocket(WS_URL);

        this.ws.onopen = () => {
            console.log(`Connected to ${WS_URL}`);
            setInterval(() => {
                this.ws.send(this.getFrame());
            }, 1000 / FPS);
        };

        this.ws.onmessage = (msg) =>  {
            if (typeof msg.data === 'string') {
                console.log("Received: '" + msg.data + "'");
            }
        };

        this.ws.onerror = (e) => {
            console.log(e.name + ": " + e.message)
        }
    }


    getFrame() {
        const video = this.videoRef;
        if (video !== null && video !== undefined) {
            const canvas = document.createElement('canvas');
            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;
            canvas.getContext('2d').drawImage(video, 0, 0);
            const data = canvas.toDataURL('image/png');
            return data;
        }
    }

    render() {
        const style = this.state.cameraAvailable ? {display: 'none'} : {};
        return (
            <div className="camera">
                <video autoPlay
                       style={{style}}
                       ref={ref => this.videoRef = ref}>
                    <p>Video stream not available.</p>
                </video>
                {!this.state.cameraAvailable && <p>Camera not available!</p>}
            </div>
        )
    }
}
