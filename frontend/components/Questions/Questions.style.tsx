import { colors } from '@/utils'
import styled from '@emotion/styled'

export const QuestionsWrapper = styled.section`
  padding: 40px 20px; /* Уменьшим отступы по бокам для мобильных */
  @media (min-width: 768px) {
    padding: 60px 40px; /* Увеличим отступы для планшетов */
  }
  @media (min-width: 1024px) {
    padding: 60px 0; /* Вернем исходные отступы для десктопов */
  }
`;

export const QuestionsItems = styled.div`
  gap: 20px; /* Увеличим общий отступ между блоками на мобильных */
  margin-top: 30px; /* Уменьшим верхний отступ на мобильных */
  align-items: start;
  display: flex;
  flex-direction: column; /* По умолчанию делаем column для мобильных */

  .left,
  .right {
    display: flex;
    flex-direction: column;
    flex-wrap: wrap;
    gap: 20px; /* Увеличим внутренний отступ между элементами */
    padding: 20px; /* Уменьшим внутренние отступы блоков */
    border: 1px solid ${colors.borderColor};
    border-radius: 15px; /* Уменьшим скругление углов */
    width: 100%;
    margin-bottom: 15px; /* Уменьшим отступ снизу */
  }

  @media (min-width: 768px) {
    gap: 24px; /* Увеличим общий отступ на планшетах */
    margin-top: 40px;
    .left,
    .right {
      gap: 12px; /* Вернем внутренний отступ блоков */
      padding: 40px; /* Вернем внутренние отступы блоков */
      border-radius: 20px 26px; /* Вернем скругление углов */
      margin-bottom: 20px;
    }
    display: flex;
    flex-direction: row; /* На планшетах делаем row для расположения left и right рядом */
    align-items: flex-start;
  }

  @media (min-width: 1024px) {
    gap: 12px;
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 616px));
    justify-content: space-between;
    .left,
    .right {
      gap: 12px;
      padding: 40px;
      border-radius: 20px 26px;
      margin-bottom: 20px;
    }
  }
`;

export const QuestionsItem = styled.div<{ isActive: boolean }>`
  cursor: pointer;
  padding: 10px 15px; /* Увеличим вертикальные и горизонтальные отступы */
  border-bottom: 1px solid ${colors.borderColor};

  div {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-right: 10px; /* Добавим отступ справа для стрелки */

    svg {
      transform: ${({ isActive }) => (isActive ? 'rotate(180deg)' : 'rotate(0deg)')};
      transition: 0.3s ease 0s;
      font-size: 20px; /* Увеличим размер стрелки */
    }
  }

  h4 {
    font-weight: 400;
    font-size: 18px; /* Увеличим размер заголовка на мобильных */
    line-height: 28px;
    color: ${({ isActive }) => (isActive ? colors.mainTextColor : colors.subtitleTextColor)};
    transition: color 0.3s ease-in-out;
    margin-bottom: 5px; /* Добавим небольшой отступ снизу заголовка */
  }

  p {
    font-weight: 400;
    font-size: 14px;
    line-height: 24px;
    color: ${colors.SecondGreyTextColor};
    margin-top: 10px; /* Добавим отступ сверху для ответа */
    padding-left: 10px; /* Небольшой отступ слева для ответа */
  }

  @media (min-width: 420px) {
    padding: 10px 20px 10px 0;
    h4 {
      font-size: 18px; /* Удерживаем размер на маленьких планшетах */
      padding-right: 20px;
    }
    p {
      font-size: 14px;
      line-height: 26px;
    }
  }

  @media (min-width: 768px) {
    padding: 15px 25px 15px 0;
    h4 {
      font-size: 20px; /* Увеличим размер заголовка на планшетах */
      padding-right: 30px;
    }
    p {
      font-size: 16px;
      line-height: 26px;
      padding-left: 15px;
    }
  }

  @media (min-width: 1440px) {
    h4 {
      font-size: 22px;
      line-height: 34px;
      padding-right: 30px;
    }
    p {
      font-size: 16px;
      line-height: 26px;
      padding-left: 0; /* Уберем левый отступ на больших экранах */
    }
  }
`;