const albumId = window.location.pathname.split('/').pop();
document.addEventListener("DOMContentLoaded", function () {
    // 실제 서버 응답(JSON)을 Ajax로 가져와서 화면에 렌더링
    axios.get(`${ctxPath}/api/music/spotify/album?albumId=${albumId}`)
        .then(response => {
            const responseData = response.data.success.responseData;
            const album = responseData.album;
            const tracks = responseData.tracks;
            const copyrights = responseData.copyrights || [];
            const label = responseData.label || "";
            const genres = responseData.genres || [];
            const popularity = responseData.popularity || 0;
            console.log(album);
            console.log(tracks);
            console.log(copyrights);
            console.log(label);
            console.log(genres);
            console.log(popularity);

        })
        .catch(err => console.error("앨범 데이터 로드 실패:", err));

    // 여기에 JSON 응답의 데이터를 받아서 동적으로 업데이트하는 예시
    const popularity = 86; // 이 값은 API에서 응답받은 popularity 값으로 변경하세요.

    const popularityBar = document.getElementById('popularity-bar');
    const popularityText = document.getElementById('popularity-text');

    // 동적으로 게이지바와 텍스트 업데이트
    popularityBar.style.width = `${popularity}%`;
    popularityText.textContent = `${popularity}% Popular`;


});
