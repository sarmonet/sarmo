import countries from 'i18n-iso-countries'
import ru from 'i18n-iso-countries/langs/ru.json'

countries.registerLocale(ru);


export const countryOptions = Object.entries(countries.getNames('ru')).map(([code, name]) => ({
  value: code,
  label: name,
}));

