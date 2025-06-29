import { colors } from '@/utils'
import styled from '@emotion/styled'

export const ModalWrapper = styled.div`
  position: fixed; 
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #0a090940;
  display: flex;
  justify-content: center; 
  align-items: center; 
  z-index: 1000;
`;
export const ModalBody = styled.div`
  display: flex;
  flex-direction: column;
  row-gap: 20px;
`
export const Modal = styled.div`
  position: relative;
  width: 80%; 
  max-width: 600px; 
  padding: 20px;
  background-color: ${colors.mainWhiteTextColor};
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  font-size: 18px;
  h1{
    font-size: 28px;
    color: ${colors.btnMainColor}
  }
  h2{
    font-size: 20px;
    margin-bottom: 10px;
  }
  h4{
    font-size: 22px;
    margin-bottom: 10px;
    line-height: 34px;
    font-weight: 400;
    transform: translateY(40%);
  }
  p{
    line-height: 145%;
    font-size: 16px;
    padding-left: 20px;
    }
  ul{
  display: flex;
  flex-direction: column;
   padding-left: 40px;
  }
  li{
    display: flex;
    flex-direction: column;
    row-gap: 10px;
    list-style-type: disc;
    margin-left: 5px;
    font-size: 16px;
    font-weight: 400;
  }
  .closeButton {
    position: absolute;
    font-size: 21px;
    top: 5%;
    right: 5%;
  }
  label{
    display: flex;
    align-items: center;
    column-gap: 20px;    
  }
  input{
    width: 20px;
    height: 20px;
  }
  .uploadButton{
    width: 100%;
    height: 56px;
    color: ${colors.mainWhiteTextColor};
    background-color: ${colors.btnSecondColor};
    border-radius: 180px;
  }
`;