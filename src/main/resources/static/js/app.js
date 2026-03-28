document.addEventListener("DOMContentLoaded", () => {
  const flash = document.querySelector("[data-autoclear]");
  if (flash) {
    setTimeout(() => flash.remove(), 3500);
  }
});
