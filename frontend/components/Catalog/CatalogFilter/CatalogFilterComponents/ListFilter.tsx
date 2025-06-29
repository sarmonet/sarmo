import React, { useState } from 'react'
import { IoIosArrowUp } from 'react-icons/io'
import { Dropdown, FilterItem, FilterWrapper, SelectWrapper } from './components.styled'

interface ListFilterProps {
  filterName: string;
  filterData: string[];
  onFilterChange: (filterName: string, value: string) => void;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  filters: any;
}

export const ListFilter: React.FC<ListFilterProps> = ({ filterName, filterData, onFilterChange, filters }) => {
  const [isOpen, setIsOpen] = useState(false);
  const value = filters?.[filterName] ?? null;

  const handleSelect = (item: string) => {
    onFilterChange(filterName, item);
    setIsOpen(false);
  };

  return (
    <FilterWrapper>
      <SelectWrapper onClick={() => setIsOpen(!isOpen)}>
        <FilterItem>
          {value || filterName}
          <IoIosArrowUp />
        </FilterItem>
        {isOpen && (
          <Dropdown>
            {filterData.map((item) => (
              <div key={item} onClick={() => handleSelect(item)}>
                {item}
              </div>
            ))}
          </Dropdown>
        )}
      </SelectWrapper>
    </FilterWrapper>
  );
};
