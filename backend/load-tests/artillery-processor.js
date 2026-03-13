module.exports = {
  setGenre,
  setUserId,
};

function setGenre(requestParams, context, ee, next) {
  const genres = ['Action', 'Comedy', 'Drama', 'Horror', 'Sci-Fi', 'Romance'];
  context.vars.genre = genres[Math.floor(Math.random() * genres.length)];
  return next();
}

function setUserId(requestParams, context, ee, next) {
  context.vars.userId = 'u' + Math.floor(Math.random() * 1000);
  return next();
}

