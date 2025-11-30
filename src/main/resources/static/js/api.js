// src/main/resources/static/js/api.js

const BASE_URL = "https://glorious-broccoli-599jx6g57jxh4rwx-8080.app.github.dev";

// 1. 토큰을 자동으로 넣어주는 fetch 함수
async function authFetch(url, options = {}) {
    const token = localStorage.getItem("accessToken");

    // 헤더 초기화
    if (!options.headers) {
        options.headers = {};
    }

    // 토큰 있으면 추가
    if (token) {
        options.headers["Authorization"] = "Bearer " + token;
    }

    // 요청 보내기
    const response = await fetch(BASE_URL + url, options);

    // 토큰 만료(403) 시 자동 로그아웃 처리
    if (response.status === 403) {
        alert("로그인 세션이 만료되었습니다.");
        logout();
        return null;
    }

    return response;
}

// 2. 로그아웃 함수 (공통 사용)
function logout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("userEmail");
    window.location.href = "login.html";
}