import Select from "react-dropdown-select";


import * as React from 'react';

const value = undefined;
export function Dropdown(props) {

  const [selectedValue, setSelectedValue] = React.useState(props?.value ? [props?.value]: []);

  function handleClickItem(values) {
    setSelectedValue(values);
    props.onChange(values);
  }

    return (
    <div style={{width: '265px'}}>
      <Select noDataRenderer={() => {
        return <div style={{padding: '.5em'}}>ingen data </div>
      }} multi={props.multi}
              placeholder={'velg'}
              values={selectedValue}
              options={props.items}
              searchable={false}
              onChange={(values) => {
                handleClickItem(values);
              }}/>
    </div>
  );
}