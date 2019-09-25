import React from 'react';
import 'webrtc-adapter';

export default class VideoStreamer extends React.Component {

    constructor(props) {
        super(props);
        this.videoRef = null;
        this.state = {
            cameraAvailable: false
        }
    }

    componentDidMount() {
        navigator.mediaDevices
            .getUserMedia({video: true})
            .then((stream) => {
                this.videoRef.srcObject = stream;

                this.setState({cameraAvailable: true})
                // this.videoRef.start();
            })
            .catch(e => {
                console.error(e.name + ": " + e.message)
            })
    }

    render() {

        const style = this.state.cameraAvailable ? { display: 'none'} : {};
        return (
            <div className="camera">
                <video autoPlay
                       style={{style}}
                       ref = {ref => this.videoRef = ref}>
                    <p>Video stream not available.</p>
                </video>
                {!this.state.cameraAvailable && <p>Camera not available!</p>}
            </div>
        )
    }
}
