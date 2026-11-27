/**
 * 재접속을 위한 게임 참여 정보 저장소 (localStorage).
 * 브라우저를 닫았다 다시 열어도 진행 중인 게임으로 돌아갈 수 있도록 한다.
 */
const PARTICIPANT_KEY = "uknowhat.participant";
const EXAMINER_KEY = "uknowhat.examiner";
const TTL_MS = 24 * 60 * 60 * 1000;

function save(key, value) {
  try {
    localStorage.setItem(key, JSON.stringify({ ...value, savedAt: Date.now() }));
  } catch (e) {
    // 저장소를 사용할 수 없는 환경(사생활 보호 모드 등)에서는 재접속만 불가
  }
}

function load(key) {
  try {
    const raw = localStorage.getItem(key);
    if (!raw) return null;
    const value = JSON.parse(raw);
    if (!value || !value.savedAt || Date.now() - value.savedAt > TTL_MS) {
      localStorage.removeItem(key);
      return null;
    }
    return value;
  } catch (e) {
    return null;
  }
}

function clear(key) {
  try {
    localStorage.removeItem(key);
  } catch (e) {
    // ignore
  }
}

export function createToken() {
  try {
    const bytes = new Uint8Array(16);
    window.crypto.getRandomValues(bytes);
    return Array.from(bytes, (b) => b.toString(16).padStart(2, "0")).join("");
  } catch (e) {
    return Date.now().toString(16) + Math.random().toString(16).slice(2);
  }
}

/** { roomNum, nickname, token } */
export const saveParticipant = (value) => save(PARTICIPANT_KEY, value);
export const loadParticipant = () => load(PARTICIPANT_KEY);
export const clearParticipant = () => clear(PARTICIPANT_KEY);

/** { roomId, roomNum, examinerId } */
export const saveExaminer = (value) => save(EXAMINER_KEY, value);
export const loadExaminer = () => load(EXAMINER_KEY);
export const clearExaminer = () => clear(EXAMINER_KEY);

/** axios 오류에서 서버 메시지 추출 */
export function errorMessage(error, fallback) {
  if (error && error.response && error.response.data && error.response.data.message)
    return error.response.data.message;
  return fallback || "요청 처리 중 오류가 발생했습니다.";
}
