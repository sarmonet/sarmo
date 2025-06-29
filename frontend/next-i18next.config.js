// next-i18next.config.js
// eslint-disable-next-line @typescript-eslint/no-require-imports
const path = require('path')

module.exports = {
  i18n: {
    defaultLocale: 'ru',
    locales: ['ru', 'en', 'uz'],
    localeDetection: false,
  },
  localePath: path.resolve('./public/locales'),
};