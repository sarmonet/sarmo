import React from 'react'
import { FilterWrapper } from './components.styled'

interface BooleanFilterProps {
  filterName: string;
  onFilterChange: (filterName: string, value: boolean) => void;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  filters: any;
}

export const BooleanFilter: React.FC<BooleanFilterProps> = ({ filterName, onFilterChange, filters }) => {
  const value = filters?.[filterName] ?? null;

  const handleTrueClick = () => {
    onFilterChange(filterName, true);
  };

  const handleFalseClick = () => {
    onFilterChange(filterName, false);
  };

  return (
    <FilterWrapper>
      <button onClick={handleTrueClick} style={{ backgroundColor: value === true ? 'lightgreen' : '#fcfcfc' }}>
        True
      </button>
      <button onClick={handleFalseClick} style={{ backgroundColor: value === false ? 'lightcoral' : '#fcfcfc' }}>
        False
      </button>
    </FilterWrapper>
  );
};
