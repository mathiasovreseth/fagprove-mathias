

import React, { useEffect } from 'react';
import { superRequest } from './App';
import { FlexDiv } from './BasicStyles';












export function CalendarPage() {
  const [data, setData] = React.useState();
  // async function GetExaminations() {
  //   const fetch = await superRequest('http://localhost:8080/examination/list', {
  //   });
  //   const res = await fetch;
  //
  //   console.log(res);
  //
  // }
  async function getExaminators() {
    const fetch = await superRequest(' http://localhost:8080/examination/calendar', {
      "startDate": "2020-03-09T00:00:00Z",
      "endDate": "2020-03-14T00:00:00Z"
    });
    const res = await fetch;

    setData(res);

  }


  useEffect(() => {
    // GetExaminations();
    getExaminators();
  }, []);

  console.log(data);
  return (
    <div>
      <FlexDiv>
        <div>Uke</div>
        <div>Dag</div>
        <div>år</div>
      </FlexDiv>
      {data && data.map((c) => {
        return (
          <div>
            <FlexDiv></FlexDiv>
          <div key={c.day}>
            <FlexDiv>
              {c.week} {' '} {c.day}
            </FlexDiv>
          </div>
          </div>
        );
      })}
    </div>
  )
}