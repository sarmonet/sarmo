import { colors } from '@/utils'
import styled from '@emotion/styled'

export const CatalogItemWrapper = styled.aside`
	border-right: 1px solid ${colors.borderColor};
  min-width: 350px;
	min-height: 100%;
	overflow: auto;
	overflow-x: hidden;
	@media(max-width: 1440px) {
		min-width: 300px;
	}
	@media(max-width: 1280px) {
		min-width: 250px;
	}
`


export const CatalogItemList = styled.ul`
	position: relative;
	display: flex;
	flex-direction: column;
	row-gap: 10px;
	margin: 40px 20px 0px 0px;

	span {
		cursor: pointer;
		font-size: 22px;
		font-weight: 400;
		line-height: 34px;
		color: ${colors.darkBlueBgColor};
    transition: all 0.3s ease 0s;
    &:hover{
      color: ${colors.btnMainColor};
    }
	}
		@media(max-width: 1440px) {
		span{
			font-size: 18px;
		}
		
	}
		
`

export const CatalogHiddenList = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 20px;
	position: absolute;
	top: 0;
	left: 0;
  border-right: 1px solid ${colors.borderColor};
  width: 377px;
  
`

export const CatalogTitle = styled.div<{ isActive: boolean , isFilter: boolean }>`
	display: flex;
	flex-direction: column;
	row-gap: 10px;

	span {
		cursor: pointer;
		color: ${({ isActive }) => (isActive ? colors.categoryLink : colors.subtitleTextColor)};
		font-size: ${({ isFilter }) => (isFilter ? '28px' : '36px')};
		font-weight: 500;
		line-height: 38px;
    transition: all 0.3s ease 0s;
    &:hover{
      color:${colors.btnMainColor};
      transform: translateX(5%);
    }
	}
`

export const CatalogSubWrapper = styled.div`
	position: relative;
	display: flex;
	flex-direction: column;
`

export const CatalogSubList = styled.div`
	display: flex;
	flex-direction: column;
	row-gap: 10px;
 
	span {
		cursor: pointer;
		font-size: 18px;
		font-weight: 500;
		line-height: 26px;
	}
`
export const SelectedSubCategory = styled.div`
  display: flex;
  align-items:center;
  column-gap: 10px;
  color: ${colors.btnMainColor};
  font-size: 22px;
  font-weight: 400;
  line-height: 34px;
  cursor: pointer;
  transition: all 0.3s ease 0s;
  &:hover{
    transform: translateX(5%);
  }
    
`


