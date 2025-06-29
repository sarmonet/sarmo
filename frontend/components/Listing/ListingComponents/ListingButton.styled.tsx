import styled from '@emotion/styled'

interface ListingButtonWrapperProps {
  bgcolor: string;
  border: string;
  color: string;
}

export const ListingButtonWrapper = styled.button<ListingButtonWrapperProps>`
  display: flex;
	justify-content: center;
  align-items: center;
  margin: 0 auto;
  column-gap: 10px;
  background-color: ${props => props.bgcolor};
  border: ${props => props.border};
  width: 100%;
  color: ${props => props.color};
  padding: 15px 20px;
  cursor: pointer;
  font-size: 18px;
	line-height: 26px;
	font-weight: 500;
	border-radius: 180px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
    &:hover {
    box-shadow: 10px 10px 10px 0px rgba(0, 0, 0, 0.1);
    }
  }
`;