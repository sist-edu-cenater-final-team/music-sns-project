document.addEventListener("DOMContentLoaded", function() {
    //마지막 경로변수 artistId 가져오기
    // 예시: const artistId = window.location.pathname.split('/').pop();
    const artistId = window.location.pathname.split('/').pop();
    axios.get(`/api/music/spotify/artist?artistId=${artistId}`)
        .then(response => {
            const artist = response.data.success.responseData;

            document.querySelector(".artist-image").src = artist.artistImageUrl;
            document.querySelector(".artist-name").textContent = artist.artistName;
            document.querySelector(".artist-genres").textContent = artist.artistGenres.join(", ");
            document.querySelector(".artist-followers").textContent = `팔로워: ${artist.totalFollowers.toLocaleString()}`;
            document.querySelector(".spotify-link").href = artist.artistSpotifyUrl;

            // 인기도 퍼센트 반영
            const popularity = artist.artistPopularity;
            const bar = document.querySelector(".popularity-bar .progress-bar");
            bar.style.width = popularity + "%";
            bar.setAttribute("aria-valuenow", popularity);
            bar.textContent = popularity + "%";

            // 색상 단계 (50 이하: 빨강, 51~75: 주황, 76~100: 초록)
            if (popularity <= 50) {
                bar.classList.remove("bg-success");
                bar.classList.add("bg-danger");
            } else if (popularity <= 75) {
                bar.classList.remove("bg-success");
                bar.classList.add("bg-warning");
            }
        })
        .catch(error => {
            console.error("아티스트 정보를 불러오는 중 오류 발생:", error);
        });

//     앨범목록
    let currentPage = 1;
    const size = 12;

    async function fetchAlbums(page) {
        try {
            const res = await fetch(`http://localhost:8080/api/music/spotify/artist/albums?artistId=${artistId}&page=${page}&size=${size}`);
            const data = await res.json();

            if (data.success && data.success.responseData) {
                const { items, hasNext } = data.success.responseData;
                renderAlbums(items);
                console.log(items);

                // hasNext 값에 따라 더보기 버튼 표시
                document.getElementById("load-more").style.display = hasNext ? "block" : "none";
            }
        } catch (err) {
            console.error("앨범 불러오기 실패:", err);
        }
    }

    function renderAlbums(albums) {
        const list = document.getElementById("album-list");

        albums.forEach(album => {
            const card = document.createElement("div");
            card.className = "album-card";
            // 아티스트들을 개별 span으로 생성
            const artistsHtml = (album.artists || []).map((artist, index) => {
                const comma = index > 0 ? ', ' : '';
                return `${comma}<span class="artist-item" data-artist-id="${artist.artistId || ''}">${artist.artistName}</span>`;
            }).join('');

            card.innerHTML = `
    <div class="album-image">   
      <img src="${album.albumImageUrl}" alt="${album.albumName}">
    </div>
      <div class="album-details">
          <div class="album-info">
              <div class="album-name">${album.albumName}</div>
              <div class="album-date">${album.releaseDate}</div>
              <div class="album-artists">${artistsHtml}</div>
          </div>
        
          <a class="spotify-link" href="${album.albumSpotifyUrl}" target="_blank">Spotify</a>
      </div>
    `;
            list.appendChild(card);
        });
    }

// 더보기 버튼 클릭 시 다음 페이지 로드
    document.getElementById("load-more").addEventListener("click", () => {
        currentPage++;
        fetchAlbums(currentPage);
    });

// 첫 페이지 로드
    fetchAlbums(currentPage);



});
