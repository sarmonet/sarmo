import { FC, ReactElement, useState } from 'react'
import { IoIosArrowUp } from 'react-icons/io'
import { DropHeader, DropList, DropWrapper } from './DropDown.style'

interface IDropDown {
  options?: { value: string; label: string }[];
  isRounded?: boolean;
  width?: string;
  isBorder?: boolean;
  onChange?: (value: string) => void;
  placeholder?: string;
  bgc?: string;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  value?: ReactElement<any, any>;
}

export const DropDown: FC<IDropDown> = ({ options, value ,bgc, isRounded = false, width, isBorder = false, onChange, placeholder }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [selected, setSelected] = useState( placeholder || value || 'Выберите опцию');

  const toggleDropdown = () => setIsOpen(!isOpen);

  const selectOption = (option: { value: string; label: string }) => {
    setSelected(option.label);
    setIsOpen(false);
    if (onChange) {
      onChange(option.value);
    }
  };

  return (
    <DropWrapper>
      <DropHeader onClick={toggleDropdown} bgc={bgc || ''} isRounded={isRounded} width={width || ''} isBorder={isBorder}> 
        {selected}
        <IoIosArrowUp className={isOpen ? 'rotate' : ''} />
      </DropHeader>

      {isOpen && (
        <DropList isRounded={isRounded}>
          {options?.map((option, index) => (
            <li key={index} onClick={() => selectOption(option)}>
              {option.label}
            </li>
          ))}
        </DropList>
      )}
    </DropWrapper>
  );
};