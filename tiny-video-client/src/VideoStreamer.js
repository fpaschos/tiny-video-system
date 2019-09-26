import React from 'react';
import 'webrtc-adapter';

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
        const FPS = 10;

        this.ws = new WebSocket(WS_URL);

        this.ws.onopen = () => {
            console.log(`Connected to ${WS_URL}`);
            setInterval(() => {
                this.getFrame()
                    .then((frame) => this.ws.send(frame))
                    .catch(e => console.log('Unable to serialize frame'))
            }, 1000 / FPS);
        };

        this.ws.onmessage = (msg) => {
            if (typeof msg.data === 'string') {
                console.log("Received: '" + msg.data + "'");
            }
        };

        this.ws.onerror = (e) => {
            console.log(e.name + ": " + e.message)
        }
    }


    /**
     * Extracts the frame data from the video using canvas.
     * The frame data is in jpeg format and encoded as an base64 string.
     *
     * @returns a promise that when resolved yields the rawFrame as base64 string.
     */
    getFrame() {
        return new Promise((resolve, reject) => {
            const video = this.videoRef;
            if (video !== null && video !== undefined) {
                const canvas = document.createElement('canvas');
                canvas.width = video.videoWidth;
                canvas.height = video.videoHeight;
                canvas.getContext('2d').drawImage(video, 0, 0);
                canvas.toBlob((blob) => {
                    if (blob instanceof Blob) {
                        let reader = new FileReader();
                        reader.readAsDataURL(blob);
                        reader.onloadend = () => {
                            let base64Url = reader.result;
                            resolve(base64Url)
                        }
                    } else {
                        reject(new Error('Frame is not a valid blob', blob))
                    }
                }, 'image/jpeg');
                // const data = canvas.toDataURL('image/png');
            }
        });
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
