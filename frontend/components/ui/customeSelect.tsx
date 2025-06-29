import { colors } from '@/utils'
import Select, { Props as SelectProps, StylesConfig } from 'react-select'

type OptionType = {
  value: string;
  label: string;
};

const customStyles: StylesConfig<OptionType, false> = {
  control: (base) => ({
    ...base,
    width: 'fit-content',
    height: '55px',
    borderRadius: '180px',
    padding: '8px 12px',
    backgroundColor: `${colors.mainWhiteTextColor}`,
    boxShadow: 'none',
    cursor: 'pointer',
  }),
  valueContainer: (base) => ({
    ...base,
    padding: '0',
  }),
  indicatorsContainer: (base) => ({
    ...base,
    paddingRight: '4px',
  }),
  input: (base) => ({
    ...base,
    margin: 0,
    padding: 0,
  }),
  placeholder: (base) => ({
    ...base,
    color: `${colors.mainTextColor}`,
    fontSize: '16px',
  }),
  singleValue: (base) => ({
    ...base,
    fontSize: '16px',
  }),
  menu: (base) => ({
    ...base,
    borderRadius: '12px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
    marginTop: 8,
  }),
  option: (base, state) => ({
    ...base,
    backgroundColor: state.isFocused ? '#f0f0f0' : `${colors.mainWhiteTextColor}`,
    color: `${colors.mainTextColor}`,
    cursor: 'pointer',
		borderRadius: '12px',
  }),
};

export const CustomSelect = (props: SelectProps<OptionType, false>) => {
  return <Select {...props} styles={customStyles} />;
};
