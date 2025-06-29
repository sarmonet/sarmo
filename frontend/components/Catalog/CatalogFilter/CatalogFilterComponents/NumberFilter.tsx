import React, { useEffect, useState } from 'react'
import { IoIosArrowUp } from 'react-icons/io'
import { Dropdown, ErrorText, FilterItem, FilterWrapper, SelectWrapper } from './components.styled'

interface NumberFilterProps {
  filterName: string;
  filterData: { min: number; max: number };
  onFilterChange: (filterName: string, value: unknown, type?: string) => void;
  filters: Record<string, { min?: number; max?: number }>; 
  resetKey?: number;
}

export const NumberFilter: React.FC<NumberFilterProps> = ({ filterName, filterData, onFilterChange, filters, resetKey }) => {
  const [from, setFrom] = useState<number | null>(filters?.[filterName]?.min || null);
  const [to, setTo] = useState<number | null>(filters?.[filterName]?.max || null);
  const [isFromOpen, setIsFromOpen] = useState<boolean>(false);
  const [isToOpen, setIsToOpen] = useState<boolean>(false);
  const [error, setError] = useState<string>('');
  const [steps, setSteps] = useState<number[]>([]);

  useEffect(() => {
    const generateSteps = () => {
      const { min, max } = filterData;
      if (min !== undefined && max !== undefined && max > min) { 
        const step = (max - min) / 10;
        const newSteps = Array.from({ length: 11 }, (_, i) => Math.round(min + step * i));
        setSteps(newSteps);
      } else {
        setSteps([]); 
      }
    };

    generateSteps();
  }, [filterData]);

  const handleFromSelect = (value: number) => {
    setFrom(value);
    onFilterChange(filterName, value, 'from');
    setIsFromOpen(false);
    if (to !== null && value > to) {
      setError(`${filterName} 'ОТ' не может быть больше 'ДО'`);
    } else {
      setError('');
    }
  };

  const handleToSelect = (value: number) => {
    setTo(value);
    onFilterChange(filterName, value, 'to');
    setIsToOpen(false);
    if (from !== null && value < from) {
      setError(`${filterName} 'ДО' не может быть меньше 'ОТ'`);
    } else {
      setError('');
    }
  };

  return (
    <FilterWrapper key={resetKey}>
      <SelectWrapper onClick={() => setIsFromOpen(!isFromOpen)}>
        <FilterItem>
          {from !== null ? `${from}` : `${filterName} ОТ`}
          <IoIosArrowUp />
        </FilterItem>
        {isFromOpen && (
          <Dropdown>
            {steps.map((num) => (
              <div key={num} onClick={() => handleFromSelect(num)}>
                {Number(num).toLocaleString('de-DE')}
              </div>
            ))}
          </Dropdown>
        )}
      </SelectWrapper>

      <SelectWrapper onClick={() => setIsToOpen(!isToOpen)}>
        <FilterItem>
          {to !== null ? `${to}` : `${filterName} ДО`}
          <IoIosArrowUp />
        </FilterItem>
        {isToOpen && (
          <Dropdown>
            {steps.map((num) => (
              <div key={num} onClick={() => handleToSelect(num)}>
                 {Number(num).toLocaleString('de-DE')}
              </div>
            ))}
          </Dropdown>
        )}
      </SelectWrapper>

      {error && <ErrorText>{error}</ErrorText>}
    </FilterWrapper>
  );
};