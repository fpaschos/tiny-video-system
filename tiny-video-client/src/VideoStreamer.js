import React from 'react';

export default class VideoStreamer extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            test: false
        }
    }

    componentDidMount() {
        navigator.mediaDevices.getUserMedia({video: {width: 426, height: 240} })
    }

    render() {
        return (
            <p> Video stream goes here!</p>
        )
    }
}
