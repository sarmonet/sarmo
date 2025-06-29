import { colors } from '@/utils'
import styled from '@emotion/styled'

export const StyledSliderContainer = styled.div`

.slick-list {
    margin: 0 -4px; /
  }
  .slick-track {
    margin: 0 -4px;
  }


  & .slick-slide {
    padding: 0 4px; 
  }


  @media (max-width: 480px) {
    .slick-list {
      margin: 0 -2px; 
    }
    .slick-track {
      margin: 0 -2px; 
    }
    & .slick-slide {
      padding: 0 2px; 
    }
  }

   @media (max-width: 1024px) {
     .slick-list { margin: 0 -6px; } 
     .slick-track { margin: 0 -6px; }
     & .slick-slide { padding: 0 6px; }
   }
`

export const StyledSlide = styled.div`
	position: relative;

	display: flex;
	flex-direction: column;
	row-gap: 0;
	padding: 0;
	border-radius: 20px;
	min-height: 600px;
	color: ${colors.mainTextColor};
	border: 1px solid #e5e1e1;
	cursor: pointer;
	transition: box-shadow 0.3s ease;
	margin: 0;

	&:hover {
		box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
	}

	@media (max-width: 1440px) {
	}

	@media (max-width: 1024px) {
		min-height: fit-content;
	}

	@media (max-width: 480px) {
		width: 100%;
		max-width: 300px;
		min-height: 395px;
	}
`

export const StyledSlideContentWrapper = styled.div`
	display: flex;
	flex-direction: column;
	width: 100%;
	height: fit-content;

	padding: 24px 10px 24px 10px;
	row-gap: 22px;

	/* Переносим сюда медиазапросы для вертикального padding и row-gap */
	@media (max-width: 1440px) {
		padding: 20px 10px 20px 10px;
		row-gap: 20px;
	}

	@media (max-width: 1024px) {
		padding: 16px 5px 16px 5px;
		row-gap: 18px;
	}

	@media (max-width: 480px) {
		padding: 16px 5px 16px 5px;
		row-gap: 16px;
	}
`

export const StyledImageWrapper = styled.div`
	position: relative;
	width: 100%;
	padding-top: 100%;
	overflow: hidden;
	border-radius: 10px;

	img {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		object-fit: cover; /* Растягивает изображение, чтобы покрыть контейнер, сохраняя пропорции (обрезает лишнее) */
		border-radius: 10px; /* Убедитесь, что border-radius применен к самому изображению или обертке */
	}
`

export const Favorite = styled.div`
	position: absolute;
	padding: 10px;
	border-radius: 50%;
	border: 1px solid ${colors.borderColor};
	background-color: ${colors.mainWhiteTextColor};
	left: 5px;
	top: 2px;
	z-index: 10;
	transition: all 0.3s ease 0s;
	display: flex;
	justify-content: center;
	align-items: center;

	&:hover {
		transform: scale(1.1);
	}

	@media (max-width: 768px) {
		padding: 8px;
	}
`

export const Premium = styled.div`
	position: absolute;
	padding: 10px;
	border-radius: 50%;
	border: 1px solid ${colors.borderColor};
	background-color: ${colors.mainWhiteTextColor};
	right: 10px; /* ИСПРАВЛЕНО: Прижимаем к правому краю */
	top: 10px;
	z-index: 10;
	transition: all 0.3s ease 0s;
	display: flex;
	justify-content: center;
	align-items: center;

	&:hover {
		transform: scale(1.1);
	}

	@media (max-width: 768px) {
		padding: 8px;
	}
`

export const StyledTop = styled.div`
	display: flex;
	align-items: center;
	flex-wrap: wrap;
	gap: 8px;
	margin-top: 16px;
	h3 {
		font-weight: 500;
		font-size: clamp(20px, 3vw, 32px);
		line-height: clamp(22px, 3vw, 42px);
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		flex-shrink: 1;
		flex-grow: 1;
		min-width: 0;
		flex-basis: 100%;
	}

	p {
		display: inline-flex;
		justify-content: center;
		align-items: center;
		padding: 4px 10px; /* Базовый padding метки */
		border-radius: 180px;
		font-weight: 400;
		font-size: clamp(14px, 3vw, 18px);
		line-height: 145%;
		border: 1px solid ${colors.borderColor};
		color: ${colors.btnMainColor};
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: 150px;
		flex-shrink: 0;
		max-width: 100%;
	}

	@media (max-width: 480px) {
		margin-top: 12px;

		h3 {
			font-size: 18px;
			line-height: 24px;
		}
		p {
			/* Метка категории */
			font-size: 12px; /* Уменьшаем размер текста метки */
			padding: 3px 8px; /* Уменьшаем внутренние отступы метки */
			line-height: normal; /* Корректируем line-height */
		}
	}
`

export const StyledInvestorBadge = styled.p`
	color: ${colors.btnSecondColor};
	white-space: nowrap;
	flex-shrink: 0;
	min-width: 0; /* Помогает overflow: hidden работать в flex */

	@media (max-width: 480px) {
		/* Для экранов <= 480px (Mobile) */
		font-size: 12px; /* Уменьшаем размер текста метки инвестора */
		/* Если у StyledInvestorBadge был padding, уменьшите его здесь */
		/* padding: ...; */
	}
`

export const StyledAbout = styled.div`
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	margin-bottom: 16px;
	margin-top: 16px;
	p {
		font-weight: 400;
		font-size: 18px;
		line-height: 26px;
		color: ${colors.greyTextColor};
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	span {
		/* Цена */
		font-weight: 500;
		font-size: 28px; /* Базовый размер шрифта */
		line-height: 38px;
		color: ${colors.btnMainColor};
	}

	color: ${colors.mainTextColor};

	@media (max-width: 480px) {
		/* Для экранов <= 480px (Mobile) */
		margin-top: 10px; /* Уменьшенный верхний отступ */
		margin-bottom: 10px; /* Уменьшенный нижний отступ */

		/* Уменьшаем размер текста Город/Страна и Цены */
		p {
			/* Город, Страна */
			font-size: 16px; /* Уменьшаем размер шрифта */
			line-height: 22px;
		}
		span {
			/* Цена */
			font-size: 22px; /* Уменьшаем размер шрифта */
			line-height: 30px;
		}
	}
`

export const ListingInfo = styled.ul`
	display: flex;
	flex-direction: column;
	li {
		font-size: 18px; /* Базовый размер шрифта */
		line-height: 26px;
		font-weight: 400;
		color: ${colors.subtitleTextColor};
	}
	/* Добавляем стили для мобильных */
	@media (max-width: 480px) {
		/* Для экранов <= 480px (Mobile) */
		li {
			font-size: 14px; /* Уменьшаем размер текста */
			line-height: 20px;
		}
	}
`

export const HiddenBlock = styled.div`
	/* Этот блок не виден по умолчанию, его стили не влияют на длину видимой части карточки */
	position: relative;
	opacity: 0;
	visibility: hidden;
	top: -20%;
	transition: all 0.3s ease 0s;
`
