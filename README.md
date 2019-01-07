# HLS PLAYER PLUGIN
The HLS protocol provides an open, reliable, efficient means of delivering continuous video streaming (live or VOD) over the Internet using a standard HTTP transport. It allows a receiver to adapt the bitrate of the media to the current network conditions, in order to maintain uninterrupted playback at the best possible quality. Using HTTP in this way in many cases allows the content to be cached for efficient and scalable delivery to large audiences.
 #### HTTP Live Streaming process:
- The audio/video to be streamed is reproduced by a media encoder at different quality levels, bitrates and resolutions. Each version is called a variant.
- The different variants are split up into smaller Media Segment Files.
- The encoder creates a Media Playlist for each variant with the URLs of each Media Segment.
- The encoder creates a Master Playlist File with the URLs of each Media Playlist.
To play, the client first downloads the Master Playlist, and then the Media Playlists. Then, they play each Media Segment declared within the chosen Media Playlist. The client can reload the Playlist to discover any added segments. This is needed in cases of live events, for example.
## How the plugin works
### Concept
This plugin handles HLS playback complexity internally as an intelligent logic controller test element. After getting the master playlist file, a variant stream is chosen according the criteria configured in each Media Playlist Sampler, which in-turn gets the media playlist file, the segments, etc. The plugin simulates typical client player logic, and correspondingly presents the equivalent network load as a browser-based player would. Individual media playlist samplers provide fine grain control of stream type(video, audio, closed-captions), and can select played content based on network bandwidth, media advertised resolution, or custom criteria.

### To Create Your Test
- Install the HLS Player plugin from the Plugins Manager
- Create a Thread Group.
- Add the HLS Player Add -> Logic Controller -> HLS Player
- Add the Media Playlist Sampler Add -> Sampler -> Media Playlist Sampler

After that you can add assertions, listeners, etc.

### Debugging
Set debug output in log4j2.xml:
    <Logger name="com.ramp.jmeter" level="debug"/>

### HLS Player Properties
The following properties can be set in the HLS Player.
#### Video options
Set the link to the master playlist file
- URL

#### Play options
Set the playback time of the test:
- Whole video
- Video duration (seconds)

### Media Playlist Sampler Properties
The following properties can be set in the Media Playlist Sampler. They should be tuned to simulate the real scenario you want to test.

#### Bandwidth
Select the bandwidth you want to simulate in your test. If there is only one playlist for the selected bandwidth, the plugin will select the playlist based only on this criterion.
- Custom Bandwidth (bits/s)
- Min bandwidth available
- Max bandwidth available

#### Resolution
After selecting the desired bandwidth you can select a resolution to simulate your specific device. For Custom Resolution provide the width by the height in pixels, for example "1920x1080"
- Custom Resolution (Width x Height)
- Min Resolution
- Max Resolution

## Results
You can set listeners to evaluate the results of your tests (e.g., Graph Results). The View Results Tree Listener can be used to display the HLS HTTP transactions, to observe details of the requests and responses involved (debug only).

## Acknowledgement
This plugin was initially based on the Blazemeter HLS Plugin, but has changed form considerably in this implementation. Thanks to Blazemeter for their initial work in this area.
